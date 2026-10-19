#include "environment.hpp"

#include <array>
#include <cstdlib>
#include <filesystem>
#include <map>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>

#include "driver.hpp"
#include "suite_policy.hpp"

namespace conformance::driver {
using namespace aspartame;

std::map<std::string, std::string> baseEnvironment() {
  constexpr auto inherited = std::to_array<std::string_view>(
      {"HOME", "USER", "LOGNAME", "TMPDIR", "TMP", "TEMP", "LANG", "LC_ALL", "DISPLAY", "WAYLAND_DISPLAY", "XDG_RUNTIME_DIR"});
  auto result = concat(inherited, SuiteEnvironmentKeys) ^ collect_to<std::map>([](std::string_view name) {
                  const auto *value = std::getenv(std::string(name).c_str());
                  return value ? std::optional{std::pair{std::string(name), std::string(value)}} : std::nullopt;
                });
  result["PATH"] = "/usr/bin:/bin:/usr/sbin:/sbin";
  return result;
}
void prepareRuntimeEnvironment(std::map<std::string, std::string> &environment, const std::filesystem::path &artifact,
                               const std::filesystem::path &support, std::string_view backend) {
  environment["CONFORMANCE_SUPPORT_ROOT"] = support.string();
  // This map is used with env::empty, so absent variables really are absent.
  environment = environment ^ filter_keys([](const std::string &key) {
                  return !reservedEnvironmentKey(key) || key.starts_with("CONFORMANCE_") || key == "PATH";
                });
  environment["OMP_TARGET_OFFLOAD"] = "MANDATORY";
  environment["LD_LIBRARY_PATH"] = (artifact / "lib").string() + ":" + (support / "lib").string();
  if (std::filesystem::is_regular_file(support / "data/libdrm/amdgpu.ids"))
    environment["AMDGPU_ASIC_ID_TABLE_PATHS"] = (support / "data/libdrm").string();
  if (std::filesystem::is_directory(support / "lib/rocblas/library"))
    environment["ROCBLAS_TENSILE_LIBPATH"] = (support / "lib/rocblas/library").string();
  if (backend == "cuda" || backend == "openacc") environment["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID";
  if (backend == "sycl") {
    environment.try_emplace("OverrideDefaultFP64Settings", "1");
    environment.try_emplace("IGC_EnableDPEmulation", "1");
    const auto entries = std::filesystem::is_directory(support / "lib")
                             ? std::vector<std::filesystem::directory_entry>{std::filesystem::directory_iterator(support / "lib"),
                                                                             std::filesystem::directory_iterator{}}
                             : std::vector<std::filesystem::directory_entry>{};
    entries ^ filter([](const std::filesystem::directory_entry &entry) {
      return entry.is_regular_file() && entry.path().filename().string().starts_with("libur_adapter_level_zero.so");
    }) ^ map([](const std::filesystem::directory_entry &entry) { return entry.path(); })
        ^ min() ^ for_each([&](const std::filesystem::path &adapter) { environment["UR_ADAPTERS_FORCE_LOAD"] = adapter.string(); });
  }
}
} // namespace conformance::driver
