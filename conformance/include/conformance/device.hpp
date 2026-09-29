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

enum class PhysicalDeviceScheme { pci, uuid, unknown };

struct PhysicalDevice {
  PhysicalDeviceScheme scheme;
  uint8_t value[16];
};

struct DeviceSelection {
  PhysicalDevice device;
  std::map<std::string, std::string> environment;
  uint64_t native_handle = 0;
};
struct DeviceProbeResult {
  std::string device;
  std::map<std::string, std::string> environment;
};
const DeviceSelection &device_selection();
PhysicalDevice physical_device();

inline PhysicalDevice make_pci_device(uint32_t domain, uint8_t bus, uint8_t device, uint8_t function) {
  PhysicalDevice result{PhysicalDeviceScheme::pci, {}};
  result.value[0] = static_cast<uint8_t>(domain >> 24);
  result.value[1] = static_cast<uint8_t>(domain >> 16);
  result.value[2] = static_cast<uint8_t>(domain >> 8);
  result.value[3] = static_cast<uint8_t>(domain);
  result.value[4] = bus;
  result.value[5] = device;
  result.value[6] = function;
  return result;
}

inline PhysicalDevice make_uuid_device(const uint8_t *uuid) {
  PhysicalDevice result{PhysicalDeviceScheme::uuid, {}};
  std::memcpy(result.value, uuid, sizeof(result.value));
  return result;
}

inline std::string physical_device_key(const PhysicalDevice &device) {
  if (device.scheme == PhysicalDeviceScheme::unknown) return "unknown";
  static constexpr char hex[] = "0123456789abcdef";
  std::string result(magic_enum::enum_name(device.scheme));
  result += '-';
  for (const auto byte : device.value) {
    result += hex[byte >> 4];
    result += hex[byte & 0x0f];
  }
  return result;
}

inline bool valid_physical_device_key(std::string_view value) {
  if (value == "unknown") return true;
  const auto separator = value.find('-');
  if (separator == std::string_view::npos) return false;
  const auto prefix = value.substr(0, separator);
  const auto scheme = magic_enum::enum_cast<PhysicalDeviceScheme>(prefix);
  if (!scheme || *scheme == PhysicalDeviceScheme::unknown) return false;
  const auto bytes = value.substr(separator + 1);
  return bytes.size() == 32 && bytes.find_first_not_of("0123456789abcdef") == std::string_view::npos;
}

inline PhysicalDevice physical_device_from_key(std::string_view value) {
  if (!valid_physical_device_key(value)) throw std::invalid_argument("invalid physical device key");
  if (value == "unknown") return {PhysicalDeviceScheme::unknown, {}};
  PhysicalDevice result{};
  const auto prefix = value.substr(0, value.find('-'));
  result.scheme = *magic_enum::enum_cast<PhysicalDeviceScheme>(prefix);
  constexpr auto digit = [](char value) -> uint8_t { return static_cast<uint8_t>(value <= '9' ? value - '0' : value - 'a' + 10); };
  const auto bytes = value.substr(value.find('-') + 1);
  for (size_t index = 0; index < 16; ++index)
    result.value[index] = static_cast<uint8_t>((digit(bytes[index * 2]) << 4) | digit(bytes[index * 2 + 1]));
  return result;
}

} // namespace conformance
