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

gemstone::DevicePreset nvidiaPreset() {
  const auto preset = gemstone::devicePreset("GeForce GTX 1080");
  if (preset) return *preset;
  throw std::runtime_error("unknown preset");
}

std::string uniqueIdHex(uint32_t ordinal) {
  const auto bytes = gemstone::deviceUuid(ordinal);
  uint64_t unique = 0;
  for (size_t index = 8; index < 16; ++index)
    unique = (unique << 8U) | bytes[index];
  char buffer[17];
  std::snprintf(buffer, sizeof(buffer), "%016llx", static_cast<unsigned long long>(unique));
  return buffer;
}

std::string uniqueIdText(uint32_t ordinal) { return "Unique ID: 0x" + uniqueIdHex(ordinal); }

std::string upperHex(std::string text) {
  for (char &character : text)
    if (character >= 'a' && character <= 'f') character = static_cast<char>(character - 'a' + 'A');
  return text;
}

std::string reversedUuidText(uint32_t ordinal) {
  const auto bytes = gemstone::deviceUuid(ordinal);
  char buffer[40];
  std::snprintf(buffer, sizeof(buffer), "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x", bytes[15], bytes[14],
                bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8], bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2],
                bytes[1], bytes[0]);
  return buffer;
}

std::string pciBusIdLong(uint32_t ordinal) {
  const auto address = gemstone::devicePciAddress(ordinal);
  char buffer[32];
  std::snprintf(buffer, sizeof(buffer), "%08x:%02x:%02x.%u", address.domain, address.bus, address.device, address.function);
  return buffer;
}

std::string toolLibraryPath() {
  std::string path(GEMSTONE_TOOL_LIBRARY_PATH);
  if (const auto inherited = gemstone::environment::text("LD_LIBRARY_PATH"); inherited && !inherited->empty())
    path.append(":").append(*inherited);
  return path;
}

std::string toolPreload() {
  std::string preload;
  if (const auto *sanitizer = GEMSTONE_SANITIZER_PRELOAD; sanitizer[0] != '\0') preload = sanitizer;
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
  gemstone_test::Environment rocmSmiLib;

public:
  explicit ToolEnvironment(const gemstone::DevicePreset &preset)
      : device(gemstone::environment::Device, std::string(preset.name).c_str()),
        target(gemstone::environment::TargetArch, std::string(preset.target).c_str()),
        libraryPath("LD_LIBRARY_PATH", toolLibraryPath().c_str()),
        preload(toolPreload().empty() ? std::nullopt : std::make_optional<gemstone_test::Environment>("LD_PRELOAD", toolPreload().c_str())),
        opencl("OCL_ICD_FILENAMES", GEMSTONE_OPENCL_LIBRARY), openclVendors("OCL_ICD_VENDORS", GEMSTONE_OPENCL_LIBRARY),
        levelZero("ZE_ENABLE_ALT_DRIVERS", GEMSTONE_LEVEL_ZERO_LIBRARY), dtif("HSA_ENABLE_DTIF", "1"),
        rocmSmiLib("ROCM_SMI_LIB_PATH", GEMSTONE_TOOL_ROCM_SMI_LIB) {}
};

std::string memoryKilobytes(const gemstone::DevicePreset &preset) { return std::to_string(preset.memoryBytes / 1024); }

std::string memoryMegabytes(const gemstone::DevicePreset &preset) { return std::to_string(preset.memoryBytes / (1024ULL * 1024ULL)); }

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
  const char *const syclLs = GEMSTONE_TOOL_SYCL_LS;
  if (syclLs[0] == '\0') SKIP("sycl-ls is not configured");
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  // the Intel loader allocates with new and frees with free when ASan is preloaded
  const gemstone_test::Environment asanOptions("ASAN_OPTIONS", "detect_leaks=0:alloc_dealloc_mismatch=0");
  const auto result = runTool(syclLs, {"--verbose"});
  INFO(result.output);
  REQUIRE((result.status == 0));
  REQUIRE_THAT(result.output, ContainsSubstring("[level_zero:gpu][level_zero:0]"));
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring("UUID              : " + uuidText(0)));
}

TEST_CASE("nvidia-smi reports the preset identity and capacity", "[tools][nvidia-smi]") {
  const auto preset = nvidiaPreset();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_NVIDIA_SMI, {});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring(pciBusIdLong(0)));
  REQUIRE_THAT(result.output, ContainsSubstring(memoryMegabytes(preset) + "MiB"));
}

TEST_CASE("nvidia-smi -q reports product name, memory, and UUID", "[tools][nvidia-smi]") {
  const auto preset = nvidiaPreset();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_NVIDIA_SMI, {"-q"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring("Product Name                          : " + std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring("Total                             : " + memoryMegabytes(preset) + " MiB"));
  REQUIRE_THAT(result.output, ContainsSubstring("GPU UUID                              : GPU-" + uuidText(0)));
  REQUIRE_THAT(result.output, ContainsSubstring("Bus Id                            : " + pciBusIdLong(0)));
}

TEST_CASE("nvidia-smi --query-gpu formats identity as CSV", "[tools][nvidia-smi]") {
  const auto preset = nvidiaPreset();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_NVIDIA_SMI, {"--query-gpu=name,uuid,pci.bus_id,memory.total", "--format=csv,noheader,nounits"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring("GPU-" + uuidText(0)));
  REQUIRE_THAT(result.output, ContainsSubstring(pciBusIdLong(0)));
  REQUIRE_THAT(result.output, ContainsSubstring(memoryMegabytes(preset)));
}

TEST_CASE("nvtop runs from the pinned driver utilities package", "[tools][nvtop]") {
  const auto preset = nvidiaPreset();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_NVTOP, {"-v"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring("nvtop version"));
}

TEST_CASE("rocm-smi reports the preset identity and capacity", "[tools][rocm-smi]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_ROCM_SMI, {});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring("0%"));
}

TEST_CASE("rocm-smi --showproductname --showbus --showuniqueid reports preset details", "[tools][rocm-smi]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_ROCM_SMI, {"--showproductname", "--showbus", "--showuniqueid"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring(gemstone::devicePciBusId(0)));
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.target)));
  REQUIRE_THAT(result.output, ContainsSubstring(uniqueIdText(0)));
}

TEST_CASE("amd-smi list reports the preset identity", "[tools][amd-smi]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_AMD_SMI, {"list"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring(gemstone::devicePciBusId(0)));
  REQUIRE_THAT(result.output, ContainsSubstring(uuidText(0)));
}

TEST_CASE("amd-smi static reports market name and capacity", "[tools][amd-smi]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_AMD_SMI, {"static"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring(std::to_string(preset.multiprocessors)));
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.target)));
  REQUIRE_THAT(result.output, ContainsSubstring(memoryMegabytes(preset) + " MB"));
  REQUIRE_THAT(result.output, ContainsSubstring("BIT_WIDTH: " + std::to_string(preset.busWidthBits)));
}

TEST_CASE("xpu-smi discovery reports the preset identity and capacity", "[tools][xpu-smi]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto result = runTool(GEMSTONE_TOOL_XPU_SMI, {"discovery", "-d", "0"});
  INFO(result.output);
  REQUIRE(result.status == 0);
  REQUIRE_THAT(result.output, ContainsSubstring(std::string(preset.name)));
  REQUIRE_THAT(result.output, ContainsSubstring("PCI BDF Address: " + gemstone::devicePciBusId(0)));
  REQUIRE_THAT(result.output, ContainsSubstring("SOC UUID: " + reversedUuidText(0)));
  REQUIRE_THAT(result.output, ContainsSubstring("Serial Number: " + uuidText(0)));
  REQUIRE_THAT(result.output, ContainsSubstring("Core Clock Rate: " + std::to_string(preset.boostMhz) + " MHz"));
  REQUIRE_THAT(result.output, ContainsSubstring("Memory Physical Size: " + memoryMegabytes(preset)));
  REQUIRE_THAT(result.output, ContainsSubstring("Memory Bus Width: " + std::to_string(preset.busWidthBits)));
}

TEST_CASE("AMD tools agree on the device unique ID", "[tools][rocminfo][rocm-smi][amd-smi]") {
  const auto preset = radeon();
  const ToolEnvironment environment(preset);
  const auto rocminfo = runTool(GEMSTONE_TOOL_ROCMINFO, {});
  INFO(rocminfo.output);
  REQUIRE(rocminfo.status == 0);
  const auto rocmSmi = runTool(GEMSTONE_TOOL_ROCM_SMI, {"--showuniqueid"});
  INFO(rocmSmi.output);
  REQUIRE(rocmSmi.status == 0);
  const auto amdSmi = runTool(GEMSTONE_TOOL_AMD_SMI, {"static"});
  INFO(amdSmi.output);
  REQUIRE(amdSmi.status == 0);
  const auto expected = uniqueIdHex(0);
  REQUIRE_THAT(rocminfo.output, ContainsSubstring("GPU-" + expected));
  REQUIRE_THAT(rocmSmi.output, ContainsSubstring("Unique ID: 0x" + expected));
  REQUIRE_THAT(amdSmi.output, ContainsSubstring("ASIC_SERIAL: 0x" + upperHex(expected)));
}
