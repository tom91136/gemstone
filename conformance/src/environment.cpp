#include "environment.hpp"

#include <algorithm>
#include <cstdlib>
#include <vector>

#include <aspartame/all.hpp>

#include "driver.hpp"

namespace conformance::driver {
using namespace aspartame;

std::map<std::string, std::string> base_environment() {
  std::map<std::string, std::string> result;
  for (const auto *name : {"HOME", "USER", "LOGNAME", "TMPDIR", "TMP", "TEMP", "LANG", "LC_ALL", "SystemRoot", "WINDIR", "COMSPEC",
                           "DISPLAY", "WAYLAND_DISPLAY", "XDG_RUNTIME_DIR"}) {
    if (const auto *value = std::getenv(name)) result[name] = value;
  }
#if defined(_WIN32)
  const auto system = result.contains("SystemRoot") ? result.at("SystemRoot") : "C:\\Windows";
  result["PATH"] = system + "\\System32;" + system;
#else
  result["PATH"] = "/usr/bin:/bin:/usr/sbin:/sbin";
#endif
  return result;
}
void prepare_runtime_environment(std::map<std::string, std::string> &environment, const std::filesystem::path &artifact,
                                 const std::filesystem::path &support, std::string_view backend) {
  environment["CONFORMANCE_SUPPORT_ROOT"] = support.string();
  // This map is used with env::empty, so absent variables really are absent.
  for (auto i = environment.begin(); i != environment.end();) {
    const auto &key = i->first;
    if (reserved_environment_key(key) && !key.starts_with("CONFORMANCE_") && key != "PATH") i = environment.erase(i);
    else ++i;
  }
#if defined(_WIN32)
  environment["PATH"] = artifact.string() + ";" + (support / "lib").string() + ";" + base_environment().at("PATH");
#elif defined(__APPLE__)
  environment["DYLD_LIBRARY_PATH"] = (artifact / "lib").string() + ":" + (support / "lib").string();
#else
  environment["LD_LIBRARY_PATH"] = (artifact / "lib").string() + ":" + (support / "lib").string();
#endif
  if (std::filesystem::is_regular_file(support / "data/libdrm/amdgpu.ids"))
    environment["AMDGPU_ASIC_ID_TABLE_PATHS"] = (support / "data/libdrm").string();
  if (std::filesystem::is_directory(support / "lib/rocblas/library"))
    environment["ROCBLAS_TENSILE_LIBPATH"] = (support / "lib/rocblas/library").string();
  if (backend == "cuda" || backend == "openacc") environment["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID";
  if (backend == "sycl") {
    environment.try_emplace("OverrideDefaultFP64Settings", "1");
    environment.try_emplace("IGC_EnableDPEmulation", "1");
    auto adapters = std::vector<std::filesystem::path>{};
    if (std::filesystem::is_directory(support / "lib")) {
      const std::vector<std::filesystem::directory_entry> entries{std::filesystem::directory_iterator(support / "lib"),
                                                                  std::filesystem::directory_iterator{}};
      adapters = entries | filter([](const std::filesystem::directory_entry &entry) {
                   return entry.is_regular_file() && entry.path().filename().string().starts_with("libur_adapter_level_zero.so");
                 })
                 | map([](const std::filesystem::directory_entry &entry) { return entry.path(); }) | to_vector();
    }
    std::sort(adapters.begin(), adapters.end());
    if (!adapters.empty()) environment["UR_ADAPTERS_FORCE_LOAD"] = adapters.front().string();
  }
}
} // namespace conformance::driver
