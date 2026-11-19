#include <array>
#include <atomic>
#include <cstddef>
#include <string>
#include <thread>
#include <vector>

#include <dlfcn.h>
#include <fcntl.h>
#include <unistd.h>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/test_support.hpp>

#include <environment_keys.hpp>
#include <sys/stat.h>
#include <sys/syscall.h>

namespace {

constexpr const char *ModulePath = "/sys/module/amdgpu";
constexpr const char *InitStatePath = "/sys/module/amdgpu/initstate";
constexpr const char *KfdPath = "/dev/kfd";

std::string readAll(int descriptor) {
  std::string text;
  std::array<char, 64> buffer{};
  for (auto count = read(descriptor, buffer.data(), buffer.size()); count > 0; count = read(descriptor, buffer.data(), buffer.size()))
    text.append(buffer.data(), static_cast<size_t>(count));
  return text;
}

int statPath(const char *path, struct stat *status) {
  static const auto function = reinterpret_cast<int (*)(const char *, struct stat *)>(dlsym(RTLD_DEFAULT, "stat"));
  return function(path, status);
}

bool rawExists(const char *path) {
  struct stat status{};
  return syscall(SYS_newfstatat, AT_FDCWD, path, &status, 0) == 0;
}

} // namespace

TEST_CASE("an AMD preset presents a live amdgpu module", "[hsa][amdgpu-state]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "Radeon RX 9070 XT");
  struct stat status{};
  REQUIRE(statPath(ModulePath, &status) == 0);
  REQUIRE(S_ISDIR(status.st_mode));
  REQUIRE(statPath(InitStatePath, &status) == 0);
  REQUIRE(S_ISREG(status.st_mode));
  REQUIRE(access(InitStatePath, R_OK) == 0);
  REQUIRE(access(InitStatePath, W_OK) != 0);

  const int descriptor = open(InitStatePath, O_RDONLY | O_CLOEXEC);
  REQUIRE(descriptor >= 0);
  REQUIRE(readAll(descriptor) == "live\n");
  close(descriptor);

  REQUIRE(openat(AT_FDCWD, InitStatePath, O_WRONLY) < 0);
}

TEST_CASE("an AMD preset presents the kfd device node", "[hsa][amdgpu-state]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "Radeon RX 9070 XT");
  struct stat status{};
  REQUIRE(statPath(KfdPath, &status) == 0);
  REQUIRE(S_ISCHR(status.st_mode));
  REQUIRE(access(KfdPath, F_OK) == 0);
  REQUIRE(access(KfdPath, R_OK | W_OK) == 0);
  REQUIRE(access(KfdPath, X_OK) != 0);
  REQUIRE(faccessat(AT_FDCWD, KfdPath, F_OK, 0) == 0);
}

TEST_CASE("a gfx target architecture presents the amdgpu state without a preset", "[hsa][amdgpu-state]") {
  const gemstone_test::Environment device(gemstone::environment::Device, nullptr);
  const gemstone_test::Environment arch(gemstone::environment::TargetArch, "gfx1030");
  struct stat status{};
  for (const char *path : {ModulePath, InitStatePath, KfdPath}) {
    REQUIRE(statPath(path, &status) == 0);
    REQUIRE(access(path, F_OK) == 0);
  }
}

TEST_CASE("the amdgpu state follows the selected preset", "[hsa][amdgpu-state]") {
  for (const char *device : {"GeForce GTX 1080", static_cast<const char *>(nullptr)}) {
    const gemstone_test::Environment selected(gemstone::environment::Device, device);
    for (const char *path : {ModulePath, InitStatePath, KfdPath}) {
      struct stat status{};
      REQUIRE((statPath(path, &status) == 0) == rawExists(path));
      REQUIRE((access(path, F_OK) == 0) == rawExists(path));
    }
  }
}

TEST_CASE("paths outside the amdgpu state pass through", "[hsa][amdgpu-state]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "Radeon RX 9070 XT");
  struct stat status{};
  REQUIRE(statPath("/", &status) == 0);
  REQUIRE(S_ISDIR(status.st_mode));
  REQUIRE(statPath("/sys/module/amdgpu/missing", &status) != 0);
  REQUIRE(statPath("/proc/self/status", &status) == 0);
  const int descriptor = open("/proc/self/status", O_RDONLY);
  REQUIRE(descriptor >= 0);
  REQUIRE(readAll(descriptor).find("Name:") == 0);
  close(descriptor);
}

TEST_CASE("concurrent threads read the presented amdgpu state", "[hsa][amdgpu-state]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "Radeon RX 9070 XT");
  std::atomic<int> failures{0};
  std::vector<std::thread> threads;
  threads.reserve(8);
  for (int index = 0; index < 8; ++index)
    threads.emplace_back([&] {
      for (int round = 0; round < 200; ++round) {
        struct stat status{};
        const int descriptor = open(InitStatePath, O_RDONLY);
        if (statPath(ModulePath, &status) != 0 || descriptor < 0 || readAll(descriptor) != "live\n") ++failures;
        if (descriptor >= 0) close(descriptor);
      }
    });
  for (auto &thread : threads)
    thread.join();
  REQUIRE(failures.load() == 0);
}
