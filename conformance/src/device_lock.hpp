#pragma once

#if defined(__APPLE__) && !defined(_DARWIN_C_SOURCE)
  #define _DARWIN_C_SOURCE
#endif

#include <cerrno>
#include <chrono>
#include <filesystem>
#include <optional>
#include <stdexcept>
#include <thread>

#include "conformance/device.hpp"

#include <fmt/format.h>

#if defined(_WIN32)
  #define WIN32_LEAN_AND_MEAN
  #include <windows.h>
#else
  #include <fcntl.h>
  #include <unistd.h>

  #include <sys/file.h>
  #include <sys/resource.h>
#endif

namespace conformance {

#if !defined(_WIN32)
// The lock descriptor is duplicated high so a later descriptor sweep in the
// runtime cannot close it and release the lock. Place it near the process soft
// limit rather than a fixed floor, so it stays clear on any descriptor table.
inline int deviceLockDescriptorFloor() {
  rlimit limit{};
  if (::getrlimit(RLIMIT_NOFILE, &limit) == 0 && limit.rlim_cur != RLIM_INFINITY && limit.rlim_cur >= 1024)
    return static_cast<int>(limit.rlim_cur / 2);
  return 900;
}
#endif

inline std::filesystem::path device_lock_path(const PhysicalDevice &device) {
  return std::filesystem::temp_directory_path() / ("gpu-conformance-" + physical_device_key(device) + ".lock");
}

class DeviceLock {
  class FileLock {
  public:
    FileLock(const std::filesystem::path &path, bool exclusive, std::chrono::steady_clock::time_point deadline) {
#if defined(_WIN32)
      file_ = CreateFileW(path.c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, nullptr, OPEN_ALWAYS,
                          FILE_ATTRIBUTE_NORMAL, nullptr);
      if (file_ == INVALID_HANDLE_VALUE) throw std::runtime_error("cannot open device lock " + path.string());
      OVERLAPPED overlap{};
      while (!LockFileEx(file_, (exclusive ? LOCKFILE_EXCLUSIVE_LOCK : 0) | LOCKFILE_FAIL_IMMEDIATELY, 0, MAXDWORD, MAXDWORD, &overlap)) {
        const auto error = GetLastError();
        if (error != ERROR_LOCK_VIOLATION || std::chrono::steady_clock::now() >= deadline) {
          CloseHandle(file_);
          file_ = INVALID_HANDLE_VALUE;
          throw std::runtime_error("cannot acquire device lock before deadline: " + path.string());
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(20));
      }
#else
      const int file = ::open(path.c_str(), O_RDWR | O_CREAT | O_CLOEXEC, 0644);
      if (file < 0) throw std::runtime_error(fmt::format("cannot open device lock {} (errno={})", path.string(), errno));
      while (::flock(file, (exclusive ? LOCK_EX : LOCK_SH) | LOCK_NB) != 0) {
        const int error = errno;
        if ((error != EWOULDBLOCK && error != EAGAIN && error != EINTR) || std::chrono::steady_clock::now() >= deadline) {
          ::close(file);
          throw std::runtime_error("cannot acquire device lock before deadline: " + path.string());
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(20));
      }
      if (const int high_file = ::fcntl(file, F_DUPFD_CLOEXEC, deviceLockDescriptorFloor()); high_file >= 0) {
        ::close(file);
        file_ = high_file;
      } else {
        file_ = file;
      }
#endif
    }

    ~FileLock() {
#if defined(_WIN32)
      if (file_ != INVALID_HANDLE_VALUE) {
        OVERLAPPED overlap{};
        UnlockFileEx(file_, 0, MAXDWORD, MAXDWORD, &overlap);
        CloseHandle(file_);
      }
#else
      if (file_ >= 0) {
        ::flock(file_, LOCK_UN);
        ::close(file_);
      }
#endif
    }

    FileLock(const FileLock &) = delete;
    FileLock &operator=(const FileLock &) = delete;

  private:
#if defined(_WIN32)
    HANDLE file_ = INVALID_HANDLE_VALUE;
#else
    int file_ = -1;
#endif
  };

  FileLock gate_;
  std::optional<FileLock> device_;

  DeviceLock(const PhysicalDevice &device, std::chrono::steady_clock::time_point deadline)
      : gate_(std::filesystem::temp_directory_path() / "gpu-conformance-global.lock", device.scheme == PhysicalDeviceScheme::unknown,
              deadline) {
    if (device.scheme != PhysicalDeviceScheme::unknown) device_.emplace(device_lock_path(device), true, deadline);
  }

public:
  explicit DeviceLock(const PhysicalDevice &device, std::chrono::milliseconds timeout = std::chrono::minutes(10))
      : DeviceLock(device, std::chrono::steady_clock::now() + timeout) {}
};

} // namespace conformance
