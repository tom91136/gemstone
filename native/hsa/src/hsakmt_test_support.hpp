#pragma once

#include <span>
#include <string>

#include <stdint.h>

// libhsakmt has no loader notification of its own; tests bind ROCr kernel objects to the execution service directly.
namespace gemstone::hsakmt {

struct LoadedKernel {
  uint64_t object = 0;
  std::string name;
  uint32_t kernargSize = 0;
};

/** Loads `image` once for the kernels not bound yet; their service manifests must match the loaded kernarg sizes. */
bool installCodeObject(std::span<const uint8_t> image, std::span<const LoadedKernel> kernels);
std::string loaderError();

} // namespace gemstone::hsakmt
