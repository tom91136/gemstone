#include <exception>
#include <iostream>
#include <string>
#include <string_view>

#include <aspartame/all.hpp>

#include "conformance/device.hpp"

#include <fmt/format.h>

namespace {
std::string quoted(std::string_view text) {
  std::string out = "\"";
  for (const char c : text) {
    if (c == '"' || c == '\\') out += {'\\', c};
    else if (static_cast<unsigned char>(c) < 0x20) out += fmt::format("\\u{:04x}", static_cast<unsigned char>(c));
    else out += c;
  }
  return out + '"';
}
} // namespace

int main() {
  using namespace aspartame;
  try {
    const auto &selection = conformance::deviceSelection();
    const auto environment = selection.environment
                             | map([](const std::string &key, const std::string &value) { return quoted(key) + ":" + quoted(value); })
                             | mk_string(",");
    std::cout << fmt::format(R"({{"device":{},"environment":{{{}}}}})", quoted(conformance::physicalDeviceKey(selection.device)),
                             environment)
              << '\n';
    return 0;
  } catch (const std::exception &error) {
    std::cerr << "conformance-device: " << error.what() << '\n';
    return 1;
  }
}
