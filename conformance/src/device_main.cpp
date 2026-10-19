#include <exception>
#include <iostream>

#include <glaze/json.hpp>

#include "conformance/device.hpp"

int main() {
  try {
    const auto &selection = conformance::deviceSelection();
    const conformance::DeviceProbeResult result{.device = conformance::physicalDeviceKey(selection.device),
                                                .environment = selection.environment};
    std::cout << glz::write_json(result).value() << '\n';
    return 0;
  } catch (const std::exception &error) {
    std::cerr << "conformance-device: " << error.what() << '\n';
    return 1;
  }
}
