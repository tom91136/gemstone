#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"
#define CL_TARGET_OPENCL_VERSION 300
#include "memory.hpp"

namespace {
using namespace memory_patterns;
std::string deviceString(cl_device_id device, cl_device_info name) {
  size_t size{};
  check(clGetDeviceInfo(device, name, 0, nullptr, &size));
  std::string value(size, '\0');
  check(clGetDeviceInfo(device, name, size, value.data(), nullptr));
  return value;
}
// Prerequisites apply only to the selected allocation and operation.
uint64_t prerequisites(const std::string &kind, bool atomic, bool wide) {
  const auto selected = conformance::deviceSelection().nativeHandle;
  auto *device = reinterpret_cast<cl_device_id>(selected);
  if (deviceString(device, CL_DEVICE_IL_VERSION).find("SPIR-V") == std::string::npos) SKIP("memory fixture requires SPIR-V program input");
  if (wide && deviceString(device, CL_DEVICE_EXTENSIONS).find("cl_khr_int64_base_atomics") == std::string::npos)
    SKIP("64-bit atomics unavailable");
  if (kind == "svm") {
    cl_device_svm_capabilities flags{};
    check(clGetDeviceInfo(device, CL_DEVICE_SVM_CAPABILITIES, sizeof(flags), &flags, nullptr));
    const cl_device_svm_capabilities needed = CL_DEVICE_SVM_FINE_GRAIN_BUFFER | (atomic ? CL_DEVICE_SVM_ATOMICS : 0);
    if ((flags & needed) != needed) SKIP("fine-grained SVM or SVM atomics unavailable");
  } else {
    if (deviceString(device, CL_DEVICE_EXTENSIONS).find("cl_intel_unified_shared_memory") == std::string::npos)
      SKIP("Intel USM extension unavailable");
    cl_device_unified_shared_memory_capabilities_intel flags{};
    const auto name = kind == "usm_host" ? CL_DEVICE_HOST_MEM_CAPABILITIES_INTEL : CL_DEVICE_SINGLE_DEVICE_SHARED_MEM_CAPABILITIES_INTEL;
    check(clGetDeviceInfo(device, name, sizeof(flags), &flags, nullptr));
    const cl_device_unified_shared_memory_capabilities_intel needed =
        CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL
        | (atomic ? CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_ATOMIC_ACCESS_INTEL
                        | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL
                  : 0);
    if ((flags & needed) != needed) SKIP("USM allocation lacks required capabilities");
  }
  return selected;
}
void execute(const char *kind, unsigned scenario) {
  const bool wide = scenario >= 11;
  const auto selected = prerequisites(kind, scenario >= 6, wide);
  Backend backend(kind, selected, scenario >= 6, wide ? "kernel64.spv" : "kernel.spv");
  runScenario(backend, scenario);
}
} // namespace

#include "cases/memory.inl"

UNIFIED_MEMORY_CASES("svm", "")
UNIFIED_MEMORY_CASES("usm_host", "[full-matrix]")
UNIFIED_MEMORY_CASES("usm_shared", "[full-matrix]")
