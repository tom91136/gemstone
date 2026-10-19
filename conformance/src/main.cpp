#include <cstddef>
#include <exception>
#include <iostream>
#include <span>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

#include <fire.hpp>

#include "driver.hpp"

static int conformanceMain(std::vector<std::string> positional = fire::arg({0, "command/targets", fire::variadic()}),
                           const std::string &profiles = fire::arg("--profiles", ""),
                           const std::string &artifacts = fire::arg("--artifacts", ""),
                           const std::string &output = fire::arg("--output", ""), const std::string &machine = fire::arg("--machine", ""),
                           std::string workload = fire::arg("--workload", "compact"), bool direct = fire::arg("--direct"),
                           const std::string &workloads = fire::arg("--workloads", ""), bool isolated = fire::arg("--isolated-launcher"),
                           bool launcher = fire::arg("--launcher")) {
  try {
    if (positional.empty()) throw std::invalid_argument("expected run");
    // Fire keeps arguments after -- positional. Its raw argument API preserves
    // launcher flags byte-for-byte without parsing or rewriting the command.
    auto &raw = fire::raw_args;
    auto launch = launcher
                      ? conformance::driver::launcherArguments(std::span<char *const>(raw.argv() + 1, static_cast<size_t>(raw.argc() - 1)))
                      : std::vector<std::string>{};
    if (launcher) {
      if (positional.size() <= launch.size()) throw std::invalid_argument("expected command before launcher");
      positional.resize(positional.size() - launch.size());
    }
    const auto command = positional.front();
    positional.erase(positional.begin());
    if (command == "run") {
      conformance::driver::RunOptions c;
      c.profiles = profiles;
      c.workloads = workloads;
      c.direct = direct;
      c.artifacts = artifacts;
      c.output = output;
      c.workload = std::move(workload);
      if (!machine.empty()) c.machine = machine;
      c.selectors = std::move(positional);
      c.launcher = std::move(launch);
      c.isolatedLauncher = isolated;
      return conformance::driver::run(std::move(c));
    }
    throw std::invalid_argument("unknown command: " + command);
  } catch (const std::exception &error) {
    std::cerr << "conformance: " << error.what() << '\n';
    return 2;
  }
}
FIRE(conformanceMain, "Discover and run conformance cases. Launcher syntax: run [options] [targets] --launcher -- command [arguments].")
