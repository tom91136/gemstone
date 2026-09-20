#include <exception>
#include <iostream>

#include <catch2/catch_session.hpp>

#include "conformance/device.hpp"

int main(int argc, char **argv) {
  try {
    Catch::Session session;
    if (const auto status = session.applyCommandLine(argc, argv)) return status;
    const auto &config = session.configData();
    if (!config.listTests && !config.listTags && !config.listReporters && !config.listListeners && !config.showHelp && !config.libIdentify)
      (void)conformance::physical_device();
    return session.run();
  } catch (const std::exception &error) {
    std::cerr << "unified: " << error.what() << '\n';
    return 2;
  }
}
