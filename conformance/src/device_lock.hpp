#pragma once

#if defined(__APPLE__) && !defined(_DARWIN_C_SOURCE)
  #define _DARWIN_C_SOURCE
#endif

#include <cerrno>
#include <chrono>
#include <filesystem>
#include <stdexcept>
#include <thread>

#include "conformance/device.hpp"

#if defined(_WIN32)
  #define WIN32_LEAN_AND_MEAN
  #include <windows.h>
#else
  #include <fcntl.h>
  #include <unistd.h>

  #include <sys/file.h>
#endif

namespace conformance {

inline std::filesystem::path device_lock_path(const PhysicalDevice &device) {
  return std::filesystem::temp_directory_path() / ("gpu-conformance-" + physical_device_key(device) + ".lock");
}

class DeviceLock {
public:
  explicit DeviceLock(const PhysicalDevice &device, std::chrono::milliseconds timeout = std::chrono::minutes(10)) {
    const auto deadline = std::chrono::steady_clock::now() + timeout;
    const auto path = device_lock_path(device);
#if defined(_WIN32)
    file_ = CreateFileW(path.c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, nullptr, OPEN_ALWAYS,
                        FILE_ATTRIBUTE_NORMAL, nullptr);
    if (file_ == INVALID_HANDLE_VALUE) throw std::runtime_error("cannot open device lock " + path.string());
    OVERLAPPED overlap{};
    while (!LockFileEx(file_, LOCKFILE_EXCLUSIVE_LOCK | LOCKFILE_FAIL_IMMEDIATELY, 0, MAXDWORD, MAXDWORD, &overlap)) {
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
    if (file < 0) throw std::runtime_error("cannot open device lock " + path.string() + " (errno=" + std::to_string(errno) + ")");
    while (::flock(file, LOCK_EX | LOCK_NB) != 0) {
      const int error = errno;
      if ((error != EWOULDBLOCK && error != EAGAIN && error != EINTR) || std::chrono::steady_clock::now() >= deadline) {
        ::close(file);
        throw std::runtime_error("cannot acquire device lock before deadline: " + path.string());
      }
      std::this_thread::sleep_for(std::chrono::milliseconds(20));
    }
    if (const int high_file = ::fcntl(file, F_DUPFD_CLOEXEC, 900); high_file >= 0) {
      ::close(file);
      file_ = high_file;
    } else {
      file_ = file;
    }
#endif
  }

  ~DeviceLock() {
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

  DeviceLock(const DeviceLock &) = delete;
  DeviceLock &operator=(const DeviceLock &) = delete;
  DeviceLock(DeviceLock &&) = delete;
  DeviceLock &operator=(DeviceLock &&) = delete;

private:
#if defined(_WIN32)
  HANDLE file_ = INVALID_HANDLE_VALUE;
#else
  int file_ = -1;
#endif
};

} // namespace conformance
