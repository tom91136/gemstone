#pragma once
#include <algorithm>
#include <cstddef>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <fmt/format.h>
#include <hip/hip_runtime.h>

namespace hip_support {

inline void check(hipError_t error, std::string_view operation) {
  if (error != hipSuccess) throw std::runtime_error(std::string(operation) + ": " + hipGetErrorString(error));
}

template <typename T> struct DeviceBuffer {
  T *data = nullptr;
  size_t size = 0;
  explicit DeviceBuffer(const std::vector<T> &host) : size(host.size()) {
    check(hipMalloc(reinterpret_cast<void **>(&data), std::max<size_t>(size, 1) * sizeof(T)), "allocation");
    if (size != 0) check(hipMemcpy(data, host.data(), size * sizeof(T), hipMemcpyHostToDevice), "upload");
  }
  DeviceBuffer(const DeviceBuffer &) = delete;
  DeviceBuffer &operator=(const DeviceBuffer &) = delete;
  ~DeviceBuffer() { (void)hipFree(data); }
  [[nodiscard]] std::vector<T> download() const {
    std::vector<T> host(size);
    if (size != 0) check(hipMemcpy(host.data(), data, size * sizeof(T), hipMemcpyDeviceToHost), "download");
    return host;
  }
};

inline void finish(std::string_view name) {
  check(hipGetLastError(), fmt::format("{} launch", name));
  check(hipDeviceSynchronize(), fmt::format("{} synchronize", name));
}

} // namespace hip_support
