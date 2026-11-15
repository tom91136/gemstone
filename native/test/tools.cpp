#include <array>
#include <cstdint>
#include <cstdio>
#include <optional>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <catch2/matchers/catch_matchers_string.hpp>

#include <gemstone/environment.hpp>
#include <gemstone/process.hpp>
#include <gemstone/test_support.hpp>
#include <gemstone_device_presets.hpp>

#include "../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

namespace {

using Catch::Matchers::ContainsSubstring;

constexpr std::string_view RadeonPreset = "Radeon RX 9070 XT";

gemstone::ProcessResult runTool(const std::string &program, const std::vector<std::string> &arguments) {
  std::vector<std::string> argv{program};
  argv.insert(argv.end(), arguments.begin(), arguments.end());
  return gemstone::runProcess(std::move(argv));
}

std::string uuidText(uint32_t ordinal) {
  const auto bytes = gemstone::deviceUuid(ordinal);
  char buffer[40];
  std::snprintf(buffer, sizeof(buffer), "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x", bytes[0], bytes[1],
                bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7], bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13],
                bytes[14], bytes[15]);
  return buffer;
}

gemstone::DevicePreset radeon() {
  const auto preset = gemstone::devicePreset(RadeonPreset);
  if (preset) return *preset;
  throw std::runtime_error("unknown preset");
}

std::string toolLibraryPath() {
  std::string path(GEMSTONE_TOOL_LIBRARY_PATH);
  if (const auto inherited = gemstone::environment::text("LD_LIBRARY_PATH"); inherited && !inherited->empty())
    path.append(":").append(*inherited);
  return path;
}

std::string toolPreload() {
  std::string preload(GEMSTONE_SANITIZER_PRELOAD);
  if (const auto inherited = gemstone::environment::text("LD_PRELOAD"); inherited && !inherited->empty()) {
    if (!preload.empty()) preload.append(":");
    preload.append(*inherited);
  }
  return preload;
}

gemstone_test::RpcFixture fixture;
gemstone_test::RpcEnvironment rpc(fixture);

class ToolEnvironment {
  gemstone_test::Environment device;
  gemstone_test::Environment target;
  gemstone_test::Environment libraryPath;
  std::optional<gemstone_test::Environment> preload;
  gemstone_test::Environment opencl;
  gemstone_test::Environment openclVendors;
  gemstone_test::Environment levelZero;
  gemstone_test::Environment dtif;

public:
  explicit ToolEnvironment(const gemstone::DevicePreset &preset)
      : device(gemstone::environment::Device, std::string(preset.name).c_str()),
        target(gemstone::environment::TargetArch, std::string(preset.target).c_str()),
        libraryPath("LD_LIBRARY_PATH", toolLibraryPath().c_str()),
        preload(toolPreload().empty() ? std::nullopt : std::make_optional<gemstone_test::Environment>("LD_PRELOAD", toolPreload().c_str())),
        opencl("OCL_ICD_FILENAMES", GEMSTONE_OPENCL_LIBRARY), openclVendors("OCL_ICD_VENDORS", GEMSTONE_OPENCL_LIBRARY),
        levelZero("ZE_ENABLE_ALT_DRIVERS", GEMSTONE_LEVEL_ZERO_LIBRARY), dtif("HSA_ENABLE_DTIF", "1") {}
};

std::string memoryKilobytes(const gemstone::DevicePreset &preset) { return std::to_string(preset.memoryBytes / 1024); }

} // namespace

TEST_CASE("rocminfo reports the preset identity and capacity", "[tools][rocminfo]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_ROCMINFO, {});
  INFO(result.output);
  REQUIRE((result.status == 0));
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring(gemstone::deviceHsaUuid(0)));
  REQUIRE_THAT(result.output, ContainsSubstring("Compute Unit:            " + std::to_string(preset.multiprocessors)));
  REQUIRE_THAT(result.output, ContainsSubstring("Max Clock Freq. (MHz):   " + std::to_string(preset.boostMhz)));
  REQUIRE_THAT(result.output, ContainsSubstring("Size:                    " + memoryKilobytes(preset)));
}

TEST_CASE("clinfo reports the preset identity and capacity", "[tools][clinfo]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_CLINFO, {});
  INFO(result.output);
  REQUIRE((result.status == 0));
  REQUIRE_THAT(result.output, ContainsSubstring("Device Name                                     " + std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring("Device UUID                                     " + uuidText(0)));
  REQUIRE_THAT(result.output, ContainsSubstring("Device PCI bus info (KHR)                       PCI-E, " + gemstone::devicePciBusId(0)));
  REQUIRE_THAT(result.output,
               ContainsSubstring("Max compute units                               " + std::to_string(preset.multiprocessors)));
  REQUIRE_THAT(result.output,
               ContainsSubstring("Max clock frequency                             " + std::to_string(preset.boostMhz) + "MHz"));
  REQUIRE_THAT(result.output, ContainsSubstring("Global memory size                              " + std::to_string(preset.memoryBytes)));
}

TEST_CASE("vulkaninfo reports the preset identity and memory", "[tools][vulkaninfo]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto summary = runTool(GEMSTONE_TOOL_VULKANINFO, {"--summary"});
  INFO(summary.output);
  REQUIRE((summary.status == 0));
  REQUIRE_THAT(summary.output, ContainsSubstring("deviceName         = " + std::string(preset.name)));
  REQUIRE_THAT(summary.output, ContainsSubstring("deviceUUID         = " + uuidText(0)));

  const auto full = runTool(GEMSTONE_TOOL_VULKANINFO, {});
  INFO(full.output);
  REQUIRE((full.status == 0));
  REQUIRE_THAT(full.output, ContainsSubstring("memoryHeaps[0]:"));
  REQUIRE_THAT(full.output, ContainsSubstring("size   = " + std::to_string(preset.memoryBytes)));
}

TEST_CASE("sycl-ls reports the preset identity", "[tools][sycl-ls]") {
  const std::string syclLs = GEMSTONE_TOOL_SYCL_LS;
  if (syclLs.empty()) SKIP("sycl-ls is not configured");
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(syclLs, {"--verbose"});
  INFO(result.output);
  REQUIRE((result.status == 0));
  REQUIRE_THAT(result.output, ContainsSubstring("[level_zero:gpu][level_zero:0]"));
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring("UUID              : " + uuidText(0)));
}
