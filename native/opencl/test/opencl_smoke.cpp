#define CL_USE_DEPRECATED_OPENCL_1_1_APIS
#include <algorithm>
#include <array>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <filesystem>
#include <fstream>
#include <future>
#include <string>
#include <vector>

#include <stdint.h>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

TEST_CASE("OpenCL virtual profile advertises the selected Polyregion device", "[opencl]") {
  const gemstone_test::Environment profile(gemstone::environment::Profile, "rusticl-amd");
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  std::array<char, 128> platformName{};
  std::array<char, 128> deviceName{};
  REQUIRE((clGetPlatformInfo(platform, CL_PLATFORM_NAME, platformName.size(), platformName.data(), nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_NAME, deviceName.size(), deviceName.data(), nullptr) == CL_SUCCESS));
  REQUIRE((std::string(platformName.data()) == "rusticl"));
  REQUIRE((std::string(deviceName.data()).find("AMD") != std::string::npos));
  cl_uint vendorId = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_VENDOR_ID, sizeof(vendorId), &vendorId, nullptr) == CL_SUCCESS));
  REQUIRE((vendorId == 0x1002));
}

TEST_CASE("OpenCL device exposes half precision arithmetic", "[opencl]") {
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  std::array<char, 2048> extensions{};
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, extensions.size(), extensions.data(), nullptr) == CL_SUCCESS));
  REQUIRE((std::string(extensions.data()).find("cl_khr_fp16") != std::string::npos));
  cl_device_fp_config halfConfig = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_HALF_FP_CONFIG, sizeof(halfConfig), &halfConfig, nullptr) == CL_SUCCESS));
  REQUIRE(((halfConfig & (CL_FP_ROUND_TO_NEAREST | CL_FP_INF_NAN)) == (CL_FP_ROUND_TO_NEAREST | CL_FP_INF_NAN)));
  cl_uint vectorWidth = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF, sizeof(vectorWidth), &vectorWidth, nullptr) == CL_SUCCESS));
  REQUIRE((vectorWidth > 0));
}

TEST_CASE("OpenCL IL queries expose every supported SPIR-V revision consistently", "[opencl]") {
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_IL_VERSION, 0, nullptr, &size) == CL_SUCCESS));
  std::string versions(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_IL_VERSION, versions.size(), versions.data(), nullptr) == CL_SUCCESS));
  REQUIRE((std::string(versions.c_str()) == "SPIR-V_1.0 SPIR-V_1.1 SPIR-V_1.2 SPIR-V_1.3 SPIR-V_1.4 SPIR-V_1.5 SPIR-V_1.6"));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_ILS_WITH_VERSION, 0, nullptr, &size) == CL_SUCCESS));
  std::array<cl_name_version, 7> namedVersions{};
  REQUIRE((size == sizeof(namedVersions)));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_ILS_WITH_VERSION, sizeof(namedVersions), namedVersions.data(), nullptr) == CL_SUCCESS));
  for (cl_uint minor = 0; minor < namedVersions.size(); ++minor) {
    REQUIRE((std::string(namedVersions[minor].name) == "SPIR-V"));
    REQUIRE((namedVersions[minor].version == CL_MAKE_VERSION(1, minor, 0)));
  }
}

TEST_CASE("OpenCL generated source converts to SPIR-V", "[opencl]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  for (const char *fixture : {"subgroup_bool_compare.cl", "control_break_in_cond.cl"}) {
    const auto sourcePath = std::filesystem::path(GEMSTONE_OPENCL_TEST_SOURCE_DIR) / fixture;
    std::ifstream sourceFile(sourcePath);
    REQUIRE(sourceFile.good());
    const std::string source(std::istreambuf_iterator<char>{sourceFile}, {});
    const char *sourceText = source.c_str();
    cl_program program = clCreateProgramWithSource(context, 1, &sourceText, nullptr, &error);
    REQUIRE((program && error == CL_SUCCESS));
    const cl_int build = clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr);
    size_t logSize = 0;
    REQUIRE((clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &logSize) == CL_SUCCESS));
    std::string log(logSize, '\0');
    REQUIRE((clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, log.size(), log.data(), nullptr) == CL_SUCCESS));
    INFO(fixture << ": " << log);
    REQUIRE((build == CL_SUCCESS));
    REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  }
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("concurrent OpenCL buffer ownership keeps RPC state and bytes intact", "[opencl]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE(clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS);
  REQUIRE(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS);
  cl_int error = CL_SUCCESS;
  const auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE(context);
  std::vector<std::future<bool>> workers;
  // Repeated allocations race connection lookups and exercise native address reuse.
  for (unsigned worker = 0; worker < 8; ++worker)
    workers.push_back(std::async(std::launch::async, [context, device, worker] {
      cl_int status = CL_SUCCESS;
      const auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &status);
      if (!queue) return false;
      bool valid = true;
      for (unsigned iteration = 0; iteration < 64 && valid; ++iteration) {
        std::array<unsigned, 4> input{worker, iteration, worker + iteration, 0xabcdef};
        std::array<unsigned, 4> output{};
        const auto buffer = clCreateBuffer(context, CL_MEM_COPY_HOST_PTR, sizeof(input), input.data(), &status);
        if (!buffer) {
          valid = false;
          break;
        }
        valid = clEnqueueReadBuffer(queue, buffer, CL_TRUE, 0, sizeof(output), output.data(), 0, nullptr, nullptr) == CL_SUCCESS
                && output == input;
        valid = clReleaseMemObject(buffer) == CL_SUCCESS && valid;
      }
      return clReleaseCommandQueue(queue) == CL_SUCCESS && valid;
    }));
  for (auto &worker : workers)
    CHECK(worker.get());
  REQUIRE(clReleaseContext(context) == CL_SUCCESS);
}

TEST_CASE("OpenCL facade launch and memory smoke test", "[opencl]") {
  gemstone_test::RpcFixture server;
  server.programCatalogue({{"groups", "groups"}}, {});
  const gemstone_test::RpcEnvironment environment(server);

  cl_platform_id platform = nullptr;
  cl_uint platformCount = 0;
  REQUIRE((clGetPlatformIDs(1, &platform, &platformCount) == CL_SUCCESS));
  REQUIRE((platform && platformCount == 1));
  std::array<char, 128> platformName{};
  REQUIRE((clGetPlatformInfo(platform, CL_PLATFORM_NAME, platformName.size(), platformName.data(), nullptr) == CL_SUCCESS));
  REQUIRE((std::string(platformName.data()).find("Intel") == 0));
  REQUIRE((std::string(platformName.data()).find("Graphics") != std::string::npos));
  REQUIRE((std::string(platformName.data()).find("Gemstone") != std::string::npos));

  // The Khronos loader discovers an ICD through this extension lookup, not only dlsym.
  using GetPlatforms = cl_int(CL_API_CALL *)(cl_uint, cl_platform_id *, cl_uint *);
  auto getPlatforms = reinterpret_cast<GetPlatforms>(clGetExtensionFunctionAddress("clIcdGetPlatformIDsKHR"));
  REQUIRE((getPlatforms));
  cl_platform_id discovered = nullptr;
  REQUIRE((getPlatforms(1, &discovered, nullptr) == CL_SUCCESS));
  REQUIRE((discovered == platform));

  cl_device_id device = nullptr;
  cl_uint deviceCount = 0;
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, &deviceCount) == CL_SUCCESS));
  REQUIRE((device && deviceCount == 1));
  REQUIRE((clRetainDevice(device) == CL_SUCCESS));
  REQUIRE((clReleaseDevice(device) == CL_SUCCESS));

  cl_device_pci_bus_info_khr pci{};
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_PCI_BUS_INFO_KHR, sizeof(pci), &pci, nullptr) == CL_SUCCESS));
  REQUIRE((pci.pci_domain == 0));
  REQUIRE((pci.pci_bus == 0));
  REQUIRE((pci.pci_device == 1));
  REQUIRE((pci.pci_function == 0));

  cl_ulong timerResolution = 1;
  REQUIRE(
      (clGetPlatformInfo(platform, CL_PLATFORM_HOST_TIMER_RESOLUTION, sizeof(timerResolution), &timerResolution, nullptr) == CL_SUCCESS));
  REQUIRE((timerResolution == 0));
  cl_ulong deviceTimestamp = 0, hostTimestamp = 0;
  REQUIRE((clGetDeviceAndHostTimer(device, &deviceTimestamp, &hostTimestamp) == CL_INVALID_OPERATION));
  REQUIRE((clGetDeviceAndHostTimer(device, nullptr, nullptr) == CL_INVALID_OPERATION));
  REQUIRE((clGetHostTimer(device, &hostTimestamp) == CL_INVALID_OPERATION));
  REQUIRE((clGetHostTimer(device, nullptr) == CL_INVALID_OPERATION));

  cl_uint vectorWidth = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE, sizeof(vectorWidth), &vectorWidth, nullptr) == CL_SUCCESS));
  REQUIRE((vectorWidth > 0));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE, sizeof(vectorWidth), &vectorWidth, nullptr) == CL_SUCCESS));
  REQUIRE((vectorWidth > 0));
  cl_device_fp_config doubleConfig = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_DOUBLE_FP_CONFIG, sizeof(doubleConfig), &doubleConfig, nullptr) == CL_SUCCESS));
  constexpr cl_device_fp_config requiredDouble =
      CL_FP_FMA | CL_FP_ROUND_TO_NEAREST | CL_FP_ROUND_TO_ZERO | CL_FP_ROUND_TO_INF | CL_FP_INF_NAN | CL_FP_DENORM;
  REQUIRE(((doubleConfig & requiredDouble) == requiredDouble));

  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  cl_command_queue queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  cl_context otherContext = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((otherContext && error == CL_SUCCESS));
  cl_mem foreignBuffer = clCreateBuffer(otherContext, CL_MEM_READ_WRITE, 16, nullptr, &error);
  REQUIRE((foreignBuffer && error == CL_SUCCESS));
  std::array<uint8_t, 16> foreignBytes{};
  REQUIRE((clEnqueueReadBuffer(queue, foreignBuffer, CL_TRUE, 0, foreignBytes.size(), foreignBytes.data(), 0, nullptr, nullptr)
           == CL_INVALID_CONTEXT));
  // The attached memory object retains its context after the application's reference is released.
  REQUIRE((clReleaseContext(otherContext) == CL_SUCCESS));
  size_t foreignSize = 0;
  REQUIRE((clGetMemObjectInfo(foreignBuffer, CL_MEM_SIZE, sizeof(foreignSize), &foreignSize, nullptr) == CL_SUCCESS));
  REQUIRE((foreignSize == foreignBytes.size()));
  REQUIRE((clReleaseMemObject(foreignBuffer) == CL_SUCCESS));

  const char *unbuiltSource = "__kernel void unbuilt() {}";
  cl_program unbuiltProgram = clCreateProgramWithSource(context, 1, &unbuiltSource, nullptr, &error);
  REQUIRE((unbuiltProgram && error == CL_SUCCESS));
  cl_kernel unbuiltKernel = clCreateKernel(unbuiltProgram, "unbuilt", &error);
  REQUIRE((!unbuiltKernel && error == CL_INVALID_PROGRAM_EXECUTABLE));
  REQUIRE((clReleaseProgram(unbuiltProgram) == CL_SUCCESS));

  const cl_queue_properties profilingProperties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE, 0};
  cl_command_queue profilingQueue = clCreateCommandQueueWithProperties(context, device, profilingProperties, &error);
  REQUIRE((profilingQueue && error == CL_SUCCESS));
  cl_event profilingEvent = nullptr;
  REQUIRE((clEnqueueMarkerWithWaitList(profilingQueue, 0, nullptr, &profilingEvent) == CL_SUCCESS));
  cl_ulong queued = 0, submitted = 0, started = 0, ended = 0;
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_QUEUED, sizeof(queued), &queued, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_SUBMIT, sizeof(submitted), &submitted, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_START, sizeof(started), &started, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_END, sizeof(ended), &ended, nullptr) == CL_SUCCESS));
  REQUIRE((queued <= submitted && submitted <= started && started <= ended));
  REQUIRE((clReleaseEvent(profilingEvent) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(profilingQueue) == CL_SUCCESS));

  auto *svm = static_cast<uint8_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 64, 16));
  REQUIRE((svm));
  std::fill_n(svm, 64, uint8_t{0});

  cl_mem svmBuffer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 64, svm, &error);
  REQUIRE((svmBuffer && error == CL_SUCCESS));
  cl_bool usesSvmPointer = CL_FALSE;
  REQUIRE((clGetMemObjectInfo(svmBuffer, CL_MEM_USES_SVM_POINTER, sizeof(usesSvmPointer), &usesSvmPointer, nullptr) == CL_SUCCESS));
  REQUIRE((usesSvmPointer == CL_TRUE));
  void *svmBufferMap = clEnqueueMapBuffer(queue, svmBuffer, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, 0, 64, 0, nullptr, nullptr, &error);
  REQUIRE((svmBufferMap == svm && error == CL_SUCCESS));
  REQUIRE((clEnqueueUnmapMemObject(queue, svmBuffer, svmBufferMap, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clReleaseMemObject(svmBuffer) == CL_SUCCESS));

  cl_mem oversizedSvmBuffer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 65, svm, &error);
  REQUIRE((!oversizedSvmBuffer && error == CL_INVALID_BUFFER_SIZE));

  std::array<uint8_t, 4> hostSource{1, 2, 3, 4};
  std::array<uint8_t, 4> hostDestination{};
  cl_event hostCopyEvent = nullptr;
  REQUIRE((clEnqueueSVMMemcpy(queue, CL_TRUE, hostDestination.data(), hostSource.data(), hostSource.size(), 0, nullptr, &hostCopyEvent)
           == CL_SUCCESS));
  REQUIRE((hostDestination == hostSource));
  cl_command_type hostCopyType = 0;
  REQUIRE((clGetEventInfo(hostCopyEvent, CL_EVENT_COMMAND_TYPE, sizeof(hostCopyType), &hostCopyType, nullptr) == CL_SUCCESS));
  REQUIRE((hostCopyType == CL_COMMAND_SVM_MEMCPY));
  REQUIRE((clReleaseEvent(hostCopyEvent) == CL_SUCCESS));

  const void *migrationPointers[] = {svm + 8};
  const size_t migrationSizes[] = {16};
  cl_event migrationEvent = nullptr;
  REQUIRE((clEnqueueSVMMigrateMem(queue, 1, migrationPointers, migrationSizes, CL_MIGRATE_MEM_OBJECT_HOST, 0, nullptr, &migrationEvent)
           == CL_SUCCESS));
  cl_command_type migrationType = 0;
  REQUIRE((clGetEventInfo(migrationEvent, CL_EVENT_COMMAND_TYPE, sizeof(migrationType), &migrationType, nullptr) == CL_SUCCESS));
  REQUIRE((migrationType == CL_COMMAND_SVM_MIGRATE_MEM));
  REQUIRE((clReleaseEvent(migrationEvent) == CL_SUCCESS));

  void *deferredSvm = clSVMAlloc(context, CL_MEM_READ_WRITE, 16, 16);
  REQUIRE((deferredSvm));
  cl_context foreignSvmContext = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((foreignSvmContext && error == CL_SUCCESS));
  void *foreignSvm = clSVMAlloc(foreignSvmContext, CL_MEM_READ_WRITE, 16, 16);
  REQUIRE((foreignSvm));
  const void *foreignMigrationPointers[] = {foreignSvm};
  REQUIRE((clEnqueueSVMMigrateMem(queue, 1, foreignMigrationPointers, nullptr, 0, 0, nullptr, nullptr) == CL_INVALID_VALUE));
  void *foreignFreePointers[] = {foreignSvm};
  REQUIRE((clEnqueueSVMFree(queue, 1, foreignFreePointers, nullptr, nullptr, 0, nullptr, nullptr) == CL_INVALID_VALUE));
  clSVMFree(foreignSvmContext, foreignSvm);
  REQUIRE((clReleaseContext(foreignSvmContext) == CL_SUCCESS));
  void *freePointers[] = {deferredSvm};
  cl_event freeEvent = nullptr;
  REQUIRE((clEnqueueSVMFree(queue, 1, freePointers, nullptr, nullptr, 0, nullptr, &freeEvent) == CL_SUCCESS));
  REQUIRE((clWaitForEvents(1, &freeEvent) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(freeEvent) == CL_SUCCESS));

  cl_event mapEvent = nullptr;
  REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, svm + 8, 16, 0, nullptr, &mapEvent) == CL_SUCCESS));
  REQUIRE((mapEvent));
  REQUIRE((clWaitForEvents(1, &mapEvent) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(mapEvent) == CL_SUCCESS));
  std::fill_n(svm + 8, 16, uint8_t{0x5a});

  cl_event unmapEvent = nullptr;
  REQUIRE((clEnqueueSVMUnmap(queue, svm + 8, 0, nullptr, &unmapEvent) == CL_SUCCESS));
  REQUIRE((unmapEvent));
  REQUIRE((clWaitForEvents(1, &unmapEvent) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(unmapEvent) == CL_SUCCESS));
  for (size_t index = 0; index < 8; ++index)
    REQUIRE((svm[index] == 0));
  for (size_t index = 8; index < 24; ++index)
    REQUIRE((svm[index] == 0x5a));
  for (size_t index = 24; index < 64; ++index)
    REQUIRE((svm[index] == 0));

  cl_program program = clCreateProgramWithIL(context, server.Spirv, sizeof(server.Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((server.programLoads.load() == 0));
  REQUIRE((clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((server.programLoads.load() == 1));
  cl_kernel kernel = clCreateKernel(program, "groups", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  constexpr size_t capacity = 9 * 33 + 2;
  auto *memory = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, capacity * 4, 8));
  REQUIRE((memory));
  REQUIRE((clSetKernelArgSVMPointer(kernel, 0, memory + 1) == CL_SUCCESS));
  for (bool implicitLocal : {false, true})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t chosen : {31U, 32U, 33U}) {
        if (implicitLocal && chosen != 32) continue;
        const size_t local = chosen;
        const size_t count = groups * local - (implicitLocal ? 1 : 0);
        const size_t origin = 7;
        for (uint32_t seed : {17U, 91U}) {
          REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_WRITE, memory, capacity * 4, 0, nullptr, nullptr) == CL_SUCCESS));
          std::fill(memory, memory + capacity, 0U);
          REQUIRE((clEnqueueSVMUnmap(queue, memory, 0, nullptr, nullptr) == CL_SUCCESS));
          REQUIRE((clFinish(queue) == CL_SUCCESS));
          server.expect({chosen, groups, static_cast<uint32_t>(count), 7, seed});
          REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
          cl_event event = nullptr;
          REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, &origin, &count, implicitLocal ? nullptr : &local, 0, nullptr, &event)
                   == CL_SUCCESS));
          REQUIRE((event && clWaitForEvents(1, &event) == CL_SUCCESS));
          cl_int status = CL_QUEUED;
          REQUIRE((clGetEventInfo(event, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
          REQUIRE((status == CL_COMPLETE));
          REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ, memory, capacity * 4, 0, nullptr, nullptr) == CL_SUCCESS));
          for (size_t index = 0; index < count; ++index)
            REQUIRE((memory[index + 1] == seed + origin + index));
          REQUIRE((memory[0] == 0));
          REQUIRE((std::all_of(memory + count + 1, memory + capacity, [](uint32_t value) { return value == 0; })));
          REQUIRE((clEnqueueSVMUnmap(queue, memory, 0, nullptr, nullptr) == CL_SUCCESS));
          REQUIRE((clFinish(queue) == CL_SUCCESS));
          REQUIRE((clReleaseEvent(event) == CL_SUCCESS));
        }
      }
  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  clSVMFree(context, memory);

  // Exercise the full unsigned ID range through both directions of RPC.
  constexpr cl_uint unsignedSpecId = 0xffffffffU;
  const std::array<uint32_t, 26> specializationWords = {0x07230203,
                                                        0x00010600,
                                                        0,
                                                        6,
                                                        0,
                                                        (4U << 16) | 71U,
                                                        3,
                                                        1,
                                                        unsignedSpecId,
                                                        (4U << 16) | 71U,
                                                        5,
                                                        1,
                                                        102,
                                                        (4U << 16) | 21U,
                                                        2,
                                                        32,
                                                        1,
                                                        (2U << 16) | 20U,
                                                        4,
                                                        (4U << 16) | 50U,
                                                        2,
                                                        3,
                                                        7,
                                                        (3U << 16) | 49U,
                                                        4,
                                                        5};
  cl_program specialized = clCreateProgramWithIL(context, specializationWords.data(), sizeof(specializationWords), &error);
  REQUIRE((specialized && error == CL_SUCCESS));
  server.specializationTable({{unsignedSpecId, 4}, {102, 1}});
  cl_uint constantValue = 42;
  cl_uchar boolValue = 2;
  REQUIRE((clSetProgramSpecializationConstant(specialized, 999, sizeof(constantValue), &constantValue) == CL_INVALID_SPEC_ID));
  REQUIRE((clSetProgramSpecializationConstant(specialized, unsignedSpecId, 1, &constantValue) == CL_INVALID_VALUE));
  REQUIRE((clSetProgramSpecializationConstant(specialized, unsignedSpecId, sizeof(constantValue), nullptr) == CL_INVALID_VALUE));
  REQUIRE((clSetProgramSpecializationConstant(specialized, unsignedSpecId, sizeof(constantValue), &constantValue) == CL_SUCCESS));
  REQUIRE((clSetProgramSpecializationConstant(specialized, 102, sizeof(boolValue), &boolValue) == CL_SUCCESS));
  constantValue = 99;
  boolValue = 0;
  const unsigned loadsBeforeSpecialization = server.programLoads.load();
  REQUIRE((clBuildProgram(specialized, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((server.programLoads.load() == loadsBeforeSpecialization + 1));
  auto forwarded = server.lastSpecialization();
  auto forwardedValue = [&](uint32_t id) -> const std::vector<uint8_t> * {
    const auto found = std::find_if(forwarded.begin(), forwarded.end(), [&](const auto &entry) { return entry.first == id; });
    return found == forwarded.end() ? nullptr : &found->second;
  };
  const auto *constantBytes = forwardedValue(unsignedSpecId);
  REQUIRE((constantBytes && constantBytes->size() == sizeof(constantValue)));
  cl_uint forwardedConstant = 0;
  std::memcpy(&forwardedConstant, constantBytes->data(), sizeof(forwardedConstant));
  REQUIRE((forwardedConstant == 42));
  const auto *boolBytes = forwardedValue(102);
  REQUIRE((boolBytes && boolBytes->size() == 1 && boolBytes->at(0) == 2));
  const cl_uchar falseValue = 0;
  REQUIRE((clSetProgramSpecializationConstant(specialized, 102, sizeof(falseValue), &falseValue) == CL_SUCCESS));
  REQUIRE((clBuildProgram(specialized, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  forwarded = server.lastSpecialization();
  boolBytes = forwardedValue(102);
  REQUIRE((boolBytes && boolBytes->size() == 1 && boolBytes->at(0) == 0));
  size_t originalSize = 0;
  REQUIRE((clGetProgramInfo(specialized, CL_PROGRAM_IL, 0, nullptr, &originalSize) == CL_SUCCESS));
  REQUIRE((originalSize == sizeof(specializationWords)));
  REQUIRE((clReleaseProgram(specialized) == CL_SUCCESS));

  const char *swizzleSource = R"CLC(
    __kernel void sample_test(__global char2 *source, __global char2 *dest) {
      if (get_global_id(0) != 0) return;
      char2 tmp = (char2)((char)0);
      tmp.S10 = source[26].xx;
      dest[26] = tmp;
    }
  )CLC";
  cl_program swizzleProgram = clCreateProgramWithSource(context, 1, &swizzleSource, nullptr, &error);
  REQUIRE((swizzleProgram && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(swizzleProgram, 1, &device, "-cl-std=CL3.0", nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(swizzleProgram) == CL_SUCCESS));

  for (const char *vectorBuiltinSource : {
           "__kernel void load3(__global char *source, __global char *dest) { dest[0] = vload3(0, source).z; }",
           "__kernel void store3(__global char2 *source, __global char *dest) { "
           "char3 value = (char3)(0); value.s2 = source[0].y; vstore3(value, 0, dest); }",
       }) {
    cl_program vectorBuiltinProgram = clCreateProgramWithSource(context, 1, &vectorBuiltinSource, nullptr, &error);
    REQUIRE((vectorBuiltinProgram && error == CL_SUCCESS));
    REQUIRE((clBuildProgram(vectorBuiltinProgram, 1, &device, "-cl-std=CL3.0", nullptr, nullptr) == CL_SUCCESS));
    REQUIRE((clReleaseProgram(vectorBuiltinProgram) == CL_SUCCESS));
  }

  clSVMFree(context, svm);
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
  REQUIRE((server.launches.load() == 40));
  server.check();
}

TEST_CASE("OpenCL SVM allocation rejects contradictory and unsupported flags", "[opencl][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  // The CTS "negative" SVM case only checks an oversized CL_MEM_USE_HOST_PTR buffer.
  const cl_svm_mem_flags invalid[] = {CL_MEM_READ_WRITE | CL_MEM_READ_ONLY, CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY,
                                      CL_MEM_READ_WRITE | CL_MEM_SVM_ATOMICS, // ATOMICS requires FINE_GRAIN_BUFFER.
                                      CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, cl_svm_mem_flags{1} << 63};
  for (const auto flags : invalid) {
    CAPTURE(flags);
    void *memory = clSVMAlloc(context, flags, 64, 0);
    CHECK((memory == nullptr));
    if (memory) clSVMFree(context, memory);
  }
  cl_device_svm_capabilities capabilities = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_SVM_CAPABILITIES, sizeof(capabilities), &capabilities, nullptr) == CL_SUCCESS));
  REQUIRE((capabilities & CL_DEVICE_SVM_ATOMICS));
  void *memory = clSVMAlloc(context, CL_MEM_READ_WRITE | CL_MEM_SVM_FINE_GRAIN_BUFFER | CL_MEM_SVM_ATOMICS, 64, 0);
  REQUIRE((memory != nullptr));
  clSVMFree(context, memory);
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("Intel USM queries preserve allocation kind and interior identity", "[opencl][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  cl_platform_id platform{};
  cl_device_id device{};
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error{};
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  auto foreign = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((foreign && error == CL_SUCCESS));
  for (cl_unified_shared_memory_type_intel kind : {CL_MEM_TYPE_HOST_INTEL, CL_MEM_TYPE_DEVICE_INTEL, CL_MEM_TYPE_SHARED_INTEL}) {
    CAPTURE(kind);
    auto *base = kind == CL_MEM_TYPE_HOST_INTEL     ? clHostMemAllocINTEL(context, nullptr, 64, 0, &error)
                 : kind == CL_MEM_TYPE_DEVICE_INTEL ? clDeviceMemAllocINTEL(context, device, nullptr, 64, 0, &error)
                                                    : clSharedMemAllocINTEL(context, device, nullptr, 64, 0, &error);
    REQUIRE((base && error == CL_SUCCESS));
    for (size_t offset : {size_t{0}, size_t{13}, size_t{63}}) {
      auto *pointer = static_cast<unsigned char *>(base) + offset;
      cl_unified_shared_memory_type_intel actual{};
      void *allocationBase{};
      size_t size{};
      REQUIRE((clGetMemAllocInfoINTEL(context, pointer, CL_MEM_ALLOC_TYPE_INTEL, sizeof(actual), &actual, nullptr) == CL_SUCCESS));
      CHECK((actual == kind));
      REQUIRE((clGetMemAllocInfoINTEL(context, pointer, CL_MEM_ALLOC_BASE_PTR_INTEL, sizeof(allocationBase), &allocationBase, nullptr)
               == CL_SUCCESS));
      REQUIRE((allocationBase == base));
      REQUIRE((clGetMemAllocInfoINTEL(context, pointer, CL_MEM_ALLOC_SIZE_INTEL, sizeof(size), &size, nullptr) == CL_SUCCESS));
      REQUIRE((size == 64));
    }
    cl_unified_shared_memory_type_intel unknown{};
    REQUIRE((clGetMemAllocInfoINTEL(foreign, base, CL_MEM_ALLOC_TYPE_INTEL, sizeof(unknown), &unknown, nullptr) == CL_SUCCESS));
    CHECK((unknown == CL_MEM_TYPE_UNKNOWN_INTEL));
    auto *end = static_cast<unsigned char *>(base) + 64;
    REQUIRE((clGetMemAllocInfoINTEL(context, end, CL_MEM_ALLOC_TYPE_INTEL, sizeof(unknown), &unknown, nullptr) == CL_SUCCESS));
    CHECK((unknown == CL_MEM_TYPE_UNKNOWN_INTEL));
    REQUIRE((clMemFreeINTEL(context, base) == CL_SUCCESS));
    REQUIRE((clGetMemAllocInfoINTEL(context, base, CL_MEM_ALLOC_TYPE_INTEL, sizeof(unknown), &unknown, nullptr) == CL_SUCCESS));
    CHECK((unknown == CL_MEM_TYPE_UNKNOWN_INTEL));
  }
  REQUIRE((clReleaseContext(foreign) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("Intel blocking free waits for queued allocation uses", "[opencl][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  cl_platform_id platform{};
  cl_device_id device{};
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error{};
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  for (bool failed : {false, true}) {
    for (cl_unified_shared_memory_type_intel kind : {CL_MEM_TYPE_HOST_INTEL, CL_MEM_TYPE_DEVICE_INTEL, CL_MEM_TYPE_SHARED_INTEL}) {
      CAPTURE(kind, failed);
      auto *pointer = kind == CL_MEM_TYPE_HOST_INTEL     ? clHostMemAllocINTEL(context, nullptr, 64, 0, &error)
                      : kind == CL_MEM_TYPE_DEVICE_INTEL ? clDeviceMemAllocINTEL(context, device, nullptr, 64, 0, &error)
                                                         : clSharedMemAllocINTEL(context, device, nullptr, 64, 0, &error);
      REQUIRE((pointer && error == CL_SUCCESS));
      auto gate = clCreateUserEvent(context, &error);
      REQUIRE((gate && error == CL_SUCCESS));
      uint32_t pattern = 17;
      cl_event completed{};
      REQUIRE((clEnqueueMemFillINTEL(queue, pointer, &pattern, sizeof(pattern), 64, 1, &gate, &completed) == CL_SUCCESS));
      std::promise<void> started;
      auto freeing = std::async(std::launch::async, [&] {
        started.set_value();
        return clMemBlockingFreeINTEL(context, pointer);
      });
      started.get_future().wait();
      const bool waited = freeing.wait_for(std::chrono::milliseconds(100)) == std::future_status::timeout;
      // An early free is already wrong; cancel its command instead of touching freed storage.
      REQUIRE((clSetUserEventStatus(gate, failed || !waited ? -1 : CL_COMPLETE) == CL_SUCCESS));
      const auto completionStatus = clWaitForEvents(1, &completed);
      CHECK((completionStatus == (failed || !waited ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS)));
      CHECK((waited));
      REQUIRE((freeing.wait_for(std::chrono::seconds(3)) == std::future_status::ready));
      CHECK((freeing.get() == CL_SUCCESS));
      cl_unified_shared_memory_type_intel type{};
      REQUIRE((clGetMemAllocInfoINTEL(context, pointer, CL_MEM_ALLOC_TYPE_INTEL, sizeof(type), &type, nullptr) == CL_SUCCESS));
      CHECK((type == CL_MEM_TYPE_UNKNOWN_INTEL));
      REQUIRE((clReleaseEvent(completed) == CL_SUCCESS));
      REQUIRE((clReleaseEvent(gate) == CL_SUCCESS));
    }
  }
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("Intel blocking free accounts for kernel arguments and indirect declarations", "[opencl][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  fixture.programCatalogue({{"groups", "groups"}}, {});
  const gemstone_test::RpcEnvironment environment(fixture);
  cl_platform_id platform{};
  cl_device_id device{};
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error{};
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  auto program = clCreateProgramWithIL(context, fixture.Spirv, sizeof(fixture.Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  auto *other = clSVMAlloc(context, CL_MEM_READ_WRITE, 64, 0);
  REQUIRE((other));
  for (const std::string mode :
       {"direct", "indirect-list", "cleared-list", "usm-indirect-list", "usm-cleared-list", "indirect-type", "unrelated"}) {
    CAPTURE(mode);
    const auto info = mode.starts_with("usm-") ? CL_KERNEL_EXEC_INFO_USM_PTRS_INTEL : CL_KERNEL_EXEC_INFO_SVM_PTRS;
    const bool indirectList = mode.ends_with("indirect-list");
    const bool clearedList = mode.ends_with("cleared-list");
    auto *pointer = clHostMemAllocINTEL(context, nullptr, 64, 0, &error);
    REQUIRE((pointer && error == CL_SUCCESS));
    auto kernel = clCreateKernel(program, "groups", &error);
    REQUIRE((kernel && error == CL_SUCCESS));
    REQUIRE((clSetKernelArgMemPointerINTEL(kernel, 0, mode == "direct" ? pointer : other) == CL_SUCCESS));
    uint32_t seed = 17;
    REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
    if (indirectList || clearedList) {
      REQUIRE((clSetKernelExecInfo(kernel, info, sizeof(pointer), &pointer) == CL_SUCCESS));
      CHECK((clSetKernelExecInfo(kernel, info, sizeof(pointer), nullptr) == CL_INVALID_VALUE));
      CHECK((clSetKernelExecInfo(kernel, info, 0, &pointer) == CL_INVALID_VALUE));
    }
    if (clearedList) {
      REQUIRE((clSetKernelExecInfo(kernel, info, 0, nullptr) == CL_SUCCESS));
      CHECK((clSetKernelExecInfo(kernel, info, sizeof(pointer), nullptr) == CL_INVALID_VALUE));
      CHECK((clSetKernelExecInfo(kernel, CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM, 0, nullptr) == CL_INVALID_VALUE));
    }
    if (mode == "indirect-type") {
      cl_bool enabled = CL_TRUE;
      REQUIRE((clSetKernelExecInfo(kernel, CL_KERNEL_EXEC_INFO_INDIRECT_HOST_ACCESS_INTEL, sizeof(enabled), &enabled) == CL_SUCCESS));
    }
    auto gate = clCreateUserEvent(context, &error);
    REQUIRE((gate && error == CL_SUCCESS));
    const size_t one = 1;
    cl_event completed{};
    REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &one, &one, 1, &gate, &completed) == CL_SUCCESS));
    if (indirectList) REQUIRE((clSetKernelExecInfo(kernel, info, 0, nullptr) == CL_SUCCESS));
    // Drop all caller handles: queued snapshots still own the program/context and declarations.
    REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
    std::promise<void> started;
    auto freeing = std::async(std::launch::async, [&] {
      started.set_value();
      return clMemBlockingFreeINTEL(context, pointer);
    });
    started.get_future().wait();
    const bool waited = freeing.wait_for(std::chrono::milliseconds(100)) == std::future_status::timeout;
    REQUIRE((clSetUserEventStatus(gate, -1) == CL_SUCCESS));
    CHECK((clWaitForEvents(1, &completed) == CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST));
    CHECK((waited == (mode != "unrelated" && !clearedList)));
    REQUIRE((freeing.wait_for(std::chrono::seconds(3)) == std::future_status::ready));
    CHECK((freeing.get() == CL_SUCCESS));
    REQUIRE((clReleaseEvent(completed) == CL_SUCCESS));
    REQUIRE((clReleaseEvent(gate) == CL_SUCCESS));
  }
  clSVMFree(context, other);
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL queued buffer uses reclaim storage after the last handle is released", "[opencl][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  fixture.programCatalogue({{"groups", "groups"}}, {});
  const gemstone_test::RpcEnvironment environment(fixture);
  cl_platform_id platform{};
  cl_device_id device{};
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error{};
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  auto program = clCreateProgramWithIL(context, fixture.Spirv, sizeof(fixture.Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  for (bool failed : {false, true}) {
    for (const auto mode : {"fill", "kernel-early", "kernel-late", "subbuffer"}) {
      const bool launch = std::string_view(mode).starts_with("kernel");
      CAPTURE(failed, mode);
      const auto baseline = fixture.allocationCount();
      auto buffer = clCreateBuffer(context, CL_MEM_READ_WRITE, 64, nullptr, &error);
      REQUIRE((buffer && error == CL_SUCCESS));
      if (std::string_view(mode) == "subbuffer") {
        const cl_buffer_region region{0, 64};
        auto child = clCreateSubBuffer(buffer, 0, CL_BUFFER_CREATE_TYPE_REGION, &region, &error);
        REQUIRE((child && error == CL_SUCCESS));
        REQUIRE((clReleaseMemObject(buffer) == CL_SUCCESS));
        buffer = child;
      }
      auto gate = clCreateUserEvent(context, &error);
      REQUIRE((gate && error == CL_SUCCESS));
      cl_event completed{};
      cl_kernel retainedKernel{};
      uint32_t seed = 17;
      if (launch) {
        auto kernel = clCreateKernel(program, "groups", &error);
        REQUIRE((kernel && error == CL_SUCCESS));
        REQUIRE((clSetKernelArg(kernel, 0, sizeof(buffer), &buffer) == CL_SUCCESS));
        REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
        const size_t one = 1;
        if (!failed) fixture.expect({1, 1, 1, 0, seed});
        REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &one, &one, 1, &gate, &completed) == CL_SUCCESS));
        if (std::string_view(mode) == "kernel-late") retainedKernel = kernel;
        else REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
      } else {
        REQUIRE((clEnqueueFillBuffer(queue, buffer, &seed, sizeof(seed), 0, 64, 1, &gate, &completed) == CL_SUCCESS));
      }
      REQUIRE((clReleaseMemObject(buffer) == CL_SUCCESS));
      CHECK((fixture.allocationCount() == baseline + 1));
      REQUIRE((clSetUserEventStatus(gate, failed ? -1 : CL_COMPLETE) == CL_SUCCESS));
      CHECK((clWaitForEvents(1, &completed) == (failed ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS)));
      CHECK((fixture.allocationCount() == baseline));
      if (retainedKernel) {
        REQUIRE((clReleaseKernel(retainedKernel) == CL_SUCCESS));
        CHECK((fixture.allocationCount() == baseline));
      }
      REQUIRE((clReleaseEvent(completed) == CL_SUCCESS));
      REQUIRE((clReleaseEvent(gate) == CL_SUCCESS));
    }
  }
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL event waits reject empty lists", "[opencl]") {
  cl_event event = nullptr;
  CHECK((clWaitForEvents(0, nullptr) == CL_INVALID_VALUE));
  CHECK((clWaitForEvents(1, nullptr) == CL_INVALID_VALUE));
  CHECK((clWaitForEvents(0, &event) == CL_INVALID_VALUE));
}

TEST_CASE("OpenCL finish from an event callback waits for the queue", "[opencl]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  cl_platform_id platform{};
  cl_device_id device{};
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error{};
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  auto source = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((source && error == CL_SUCCESS));
  auto target = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((target && error == CL_SUCCESS));
  auto start = clCreateUserEvent(context, &error);
  REQUIRE((start && error == CL_SUCCESS));
  auto gate = clCreateUserEvent(context, &error);
  REQUIRE((gate && error == CL_SUCCESS));
  cl_event pending{}, marker{};
  REQUIRE((clEnqueueMarkerWithWaitList(target, 1, &gate, &pending) == CL_SUCCESS));
  REQUIRE((clEnqueueMarkerWithWaitList(source, 1, &start, &marker) == CL_SUCCESS));
  struct Finish {
    cl_command_queue queue;
    std::promise<cl_int> result;
  } finish{target, {}};
  auto finished = finish.result.get_future();
  // The callback runs on a queue worker.
  REQUIRE((clSetEventCallback(
               marker, CL_COMPLETE,
               [](cl_event, cl_int, void *data) {
                 auto &state = *static_cast<Finish *>(data);
                 state.result.set_value(clFinish(state.queue));
               },
               &finish)
           == CL_SUCCESS));
  REQUIRE((clSetUserEventStatus(start, CL_COMPLETE) == CL_SUCCESS));
  CHECK((finished.wait_for(std::chrono::milliseconds(200)) == std::future_status::timeout));
  REQUIRE((clSetUserEventStatus(gate, -1) == CL_SUCCESS));
  REQUIRE((finished.wait_for(std::chrono::seconds(3)) == std::future_status::ready));
  CHECK((finished.get() == CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST));
  REQUIRE((clReleaseEvent(marker) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(pending) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(gate) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(start) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(target) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(source) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}
