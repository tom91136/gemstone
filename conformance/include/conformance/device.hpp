#pragma once

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <map>
#include <stdexcept>
#include <string>
#include <string_view>

#include <magic_enum/magic_enum.hpp>

namespace conformance {
enum class PhysicalDeviceScheme { Pci, Uuid, Unknown };
} // namespace conformance

// Physical device keys spell the scheme in lowercase.
template <>
constexpr magic_enum::customize::customize_t
magic_enum::customize::enum_name<conformance::PhysicalDeviceScheme>(conformance::PhysicalDeviceScheme value) noexcept {
  switch (value) {
    case conformance::PhysicalDeviceScheme::Pci: return "pci";
    case conformance::PhysicalDeviceScheme::Uuid: return "uuid";
    case conformance::PhysicalDeviceScheme::Unknown: return "unknown";
  }
  return invalid_tag;
}

namespace conformance {

struct PhysicalDevice {
  PhysicalDeviceScheme scheme;
  uint8_t value[16];
};

struct DeviceSelection {
  PhysicalDevice device;
  std::map<std::string, std::string> environment;
  uint64_t nativeHandle = 0;
};
struct DeviceProbeResult {
  std::string device;
  std::map<std::string, std::string> environment;
};
const DeviceSelection &deviceSelection();

inline PhysicalDevice makePciDevice(uint32_t domain, uint8_t bus, uint8_t device, uint8_t function) {
  PhysicalDevice result{.scheme = PhysicalDeviceScheme::Pci, .value = {}};
  result.value[0] = static_cast<uint8_t>(domain >> 24);
  result.value[1] = static_cast<uint8_t>(domain >> 16);
  result.value[2] = static_cast<uint8_t>(domain >> 8);
  result.value[3] = static_cast<uint8_t>(domain);
  result.value[4] = bus;
  result.value[5] = device;
  result.value[6] = function;
  return result;
}

inline PhysicalDevice makeUuidDevice(const uint8_t *uuid) {
  PhysicalDevice result{.scheme = PhysicalDeviceScheme::Uuid, .value = {}};
  std::memcpy(result.value, uuid, sizeof(result.value));
  return result;
}

inline std::string physicalDeviceKey(const PhysicalDevice &device) {
  if (device.scheme == PhysicalDeviceScheme::Unknown) return "unknown";
  static constexpr char hex[] = "0123456789abcdef";
  std::string result(magic_enum::enum_name(device.scheme));
  result += '-';
  for (const auto byte : device.value) {
    result += hex[byte >> 4];
    result += hex[byte & 0x0f];
  }
  return result;
}

inline bool validPhysicalDeviceKey(std::string_view value) {
  if (value == "unknown") return true;
  const auto separator = value.find('-');
  if (separator == std::string_view::npos) return false;
  const auto prefix = value.substr(0, separator);
  const auto scheme = magic_enum::enum_cast<PhysicalDeviceScheme>(prefix);
  if (!scheme || *scheme == PhysicalDeviceScheme::Unknown) return false;
  const auto bytes = value.substr(separator + 1);
  return bytes.size() == 32 && bytes.find_first_not_of("0123456789abcdef") == std::string_view::npos;
}

} // namespace conformance
