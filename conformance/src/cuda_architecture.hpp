#pragma once

#include <charconv>
#include <optional>
#include <string_view>

namespace conformance {

// sm_90a and sm_100f name architecture- and family-specific variants of a compute capability.
struct CudaArchitecture {
  int capability;
  char variant;

  // `value` with or without its `sm_` prefix
  static std::optional<CudaArchitecture> parse(std::string_view value) {
    if (value.starts_with("sm_")) value.remove_prefix(3);
    char variant = 0;
    if (value.ends_with('a') || value.ends_with('f')) {
      variant = value.back();
      value.remove_suffix(1);
    }
    int capability = 0;
    const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), capability);
    if (value.empty() || value.front() < '0' || value.front() > '9' || error != std::errc{} || end != value.data() + value.size())
      return std::nullopt;
    return CudaArchitecture{.capability = capability, .variant = variant};
  }

  // Architecture-specific code runs on its own compute capability only; family-specific code also runs on later minor
  // revisions of the same major.
  [[nodiscard]] bool runsOn(int device) const {
    if (variant == 'f') return device / 10 == capability / 10 && device >= capability;
    return device == capability;
  }
};

} // namespace conformance
