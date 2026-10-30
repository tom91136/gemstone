#include <cstdlib>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"
#include "conformance/environment_keys.hpp"

#include "conformance_test_config.hpp"

#if defined(CONFORMANCE_OPENMP_TARGET)
  #include <omp.h>
#endif

#if defined(CONFORMANCE_SYCL_FP64_PROBE)
  #include <sycl/sycl.hpp>
#endif

int conformance_upstream_main(int argc, char **argv);

namespace {

std::string replaceAll(std::string value, std::string_view before, std::string_view after) {
  for (size_t position = 0; (position = value.find(before, position)) != std::string::npos; position += after.size())
    value.replace(position, before.size(), after);
  return value;
}

std::string expandArgument(std::string value) {
  for (const auto *name : {conformance::environment::SupportDir, conformance::environment::ReportDir}) {
    const auto token = "${" + std::string(name) + '}';
    if (value.find(token) == std::string::npos) continue;
    const auto *configured = std::getenv(name);
    if (!configured || !*configured) throw std::runtime_error(std::string(name) + " is required by this test");
    value = replaceAll(std::move(value), token, configured);
  }
  return value;
}

} // namespace

TEST_CASE(conformance_test_identity, "[miniapp]") {
  (void)conformance::deviceSelection();
#if defined(CONFORMANCE_OPENMP_TARGET)
  // An embedded device image alone does not prove that libgomp/libomptarget
  // executed it. Reject host fallback before the upstream benchmark starts.
  REQUIRE(omp_get_num_devices() > 0);
  int onDevice = 0;
  #pragma omp target map(from : onDevice)
  {
    onDevice = !omp_is_initial_device();
  }
  REQUIRE(onDevice == 1);
#endif
#if defined(CONFORMANCE_SYCL_FP64_PROBE)
  const sycl::queue queue(sycl::gpu_selector_v);
  if (!queue.get_device().has(sycl::aspect::fp64)) SKIP("device does not provide fp64");
#endif
  std::vector<std::string> arguments{conformance_test_identity};
  for (auto argument : conformance_test_arguments())
    arguments.push_back(expandArgument(std::move(argument)));
#if defined(CONFORMANCE_OPENCL_FLAT_DEVICE_ARGUMENT)
  if (const auto *device = std::getenv(conformance::environment::OpenclDeviceIndex); device && *device) {
    arguments.emplace_back("--device");
    arguments.emplace_back(device);
  }
#endif
  std::vector<char *> forwarded;
  forwarded.reserve(arguments.size() + 1);
  for (auto &argument : arguments)
    forwarded.push_back(argument.data());
  forwarded.push_back(nullptr);
  REQUIRE(conformance_upstream_main(static_cast<int>(arguments.size()), forwarded.data()) == 0);
}
