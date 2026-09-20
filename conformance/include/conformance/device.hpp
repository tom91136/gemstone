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

enum class PhysicalDeviceScheme { pci, uuid, reg };

struct PhysicalDevice {
  PhysicalDeviceScheme scheme;
  uint8_t value[16];
};

struct DeviceSelection {
  PhysicalDevice device;
  std::map<std::string, std::string> environment;
  uint64_t native_handle = 0;
};
const DeviceSelection &device_selection();
PhysicalDevice physical_device();

inline PhysicalDevice pci_device(uint32_t domain, uint8_t bus, uint8_t device, uint8_t function) {
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

inline PhysicalDevice uuid_device(const uint8_t *uuid) {
  PhysicalDevice result{PhysicalDeviceScheme::uuid, {}};
  std::memcpy(result.value, uuid, sizeof(result.value));
  return result;
}

inline PhysicalDevice registry_device(std::string_view identity) {
  // Two seeded FNV-1a lanes give opaque platform identifiers a stable local
  // lock key without introducing a hashing dependency into staged projects.
  uint64_t lanes[]{14695981039346656037ull, 7809847782465536322ull};
  for (const auto character : identity) {
    for (auto &lane : lanes) {
      lane ^= static_cast<uint8_t>(character);
      lane *= 1099511628211ull;
    }
    lanes[1] ^= lanes[0] >> 32;
  }
  PhysicalDevice result{PhysicalDeviceScheme::reg, {}};
  for (size_t lane = 0; lane < 2; ++lane)
    for (size_t byte = 0; byte < 8; ++byte)
      result.value[lane * 8 + byte] = static_cast<uint8_t>(lanes[lane] >> (byte * 8));
  return result;
}

inline std::string physical_device_key(const PhysicalDevice &device) {
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
  const auto separator = value.find('-');
  if (separator == std::string_view::npos) return false;
  const auto prefix = value.substr(0, separator);
  if (!magic_enum::enum_cast<PhysicalDeviceScheme>(prefix)) return false;
  const auto bytes = value.substr(separator + 1);
  return bytes.size() == 32 && bytes.find_first_not_of("0123456789abcdef") == std::string_view::npos;
}

inline PhysicalDevice physical_device_from_key(std::string_view value) {
  if (!valid_physical_device_key(value)) throw std::invalid_argument("invalid physical device key");
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
