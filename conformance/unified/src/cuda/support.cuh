#pragma once

#include <cstdint>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <cuda.h>
#include <cuda_runtime_api.h>

#include <fmt/format.h>

namespace {

inline void check(cudaError_t error, std::string_view operation) {
  if (error != cudaSuccess) throw std::runtime_error(std::string(operation) + ": " + cudaGetErrorString(error));
}

inline void check(CUresult result, std::string_view operation) {
  if (result != CUDA_SUCCESS) {
    const char *name = nullptr;
    (void)cuGetErrorName(result, &name);
    throw std::runtime_error(std::string(operation) + ": " + (name ? name : "unknown driver error"));
  }
}

template <typename T> struct DeviceBuffer {
  T *data = nullptr;
  size_t size = 0;
  explicit DeviceBuffer(const std::vector<T> &host) : size(host.size()) {
    check(cudaMalloc(reinterpret_cast<void **>(&data), size * sizeof(T)), "allocation");
    check(cudaMemcpy(data, host.data(), size * sizeof(T), cudaMemcpyHostToDevice), "upload");
  }
  DeviceBuffer(const DeviceBuffer &) = delete;
  DeviceBuffer &operator=(const DeviceBuffer &) = delete;
  ~DeviceBuffer() { cudaFree(data); }
  [[nodiscard]] std::vector<T> download() const {
    std::vector<T> host(size);
    check(cudaMemcpy(host.data(), data, size * sizeof(T), cudaMemcpyDeviceToHost), "download");
    return host;
  }
};

template <typename T> struct Buffer {
  T *device = nullptr;
  explicit Buffer(const std::vector<T> &host) {
    check(cudaMalloc(reinterpret_cast<void **>(&device), host.size() * sizeof(T)), "cudaMalloc");
    check(cudaMemcpy(device, host.data(), host.size() * sizeof(T), cudaMemcpyHostToDevice), "upload");
  }
  Buffer(const Buffer &) = delete;
  Buffer &operator=(const Buffer &) = delete;
  ~Buffer() { (void)cudaFree(device); }
  [[nodiscard]] std::vector<T> download(size_t count) const {
    std::vector<T> host(count);
    check(cudaMemcpy(host.data(), device, count * sizeof(T), cudaMemcpyDeviceToHost), "download");
    return host;
  }
};

inline void finish(std::string_view name) {
  check(cudaGetLastError(), fmt::format("{} launch", name));
  check(cudaDeviceSynchronize(), fmt::format("{} synchronize", name));
}

inline int capability() {
  int device = 0;
  int major = 0;
  int minor = 0;
  check(cudaGetDevice(&device), "device");
  check(cudaDeviceGetAttribute(&major, cudaDevAttrComputeCapabilityMajor, device), "capability");
  check(cudaDeviceGetAttribute(&minor, cudaDevAttrComputeCapabilityMinor, device), "capability");
  return (major * 10) + minor;
}

template <typename T> void expectEqual(const std::vector<T> &actual, const std::vector<T> &expected, std::string_view name) {
  for (size_t i = 0; i < expected.size(); ++i)
    if (actual[i] != expected[i])
      throw std::runtime_error(fmt::format("{} mismatch at {}: expected {:#x}, got {:#x}", name, i, static_cast<uint64_t>(expected[i]),
                                           static_cast<uint64_t>(actual[i])));
}

} // namespace
