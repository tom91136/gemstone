#include <exception>
#include <iostream>

#include <glaze/json.hpp>

#include "conformance/device.hpp"

int main() {
  try {
    const auto &selection = conformance::device_selection();
    const conformance::DeviceProbeResult result{conformance::physical_device_key(selection.device), selection.environment};
    std::cout << glz::write_json(result).value() << '\n';
    return 0;
  } catch (const std::exception &error) {
    std::cerr << "conformance-device: " << error.what() << '\n';
    return 1;
  }
}
