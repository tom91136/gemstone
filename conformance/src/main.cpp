#include <iostream>
#include <stdexcept>

#include <fire.hpp>

#include "driver.hpp"

int conformance_main(std::vector<std::string> positional = fire::arg({0, "command/targets", fire::variadic()}),
                     std::string profiles = fire::arg("--profiles", ""), std::string artifacts = fire::arg("--artifacts", ""),
                     std::string output = fire::arg("--output", ""), std::string machine = fire::arg("--machine", ""),
                     std::string workload = fire::arg("--workload", "compact"), std::string ctest = fire::arg("--ctest", "ctest"),
                     bool direct = fire::arg("--direct"), std::string workloads = fire::arg("--workloads", ""),
                     bool isolated = fire::arg("--isolated-launcher"), bool launcher = fire::arg("--launcher"),
                     std::string config = fire::arg("--config", ""), std::string program = fire::arg("--program", ""),
                     std::string test = fire::arg("--test", ""),
                     uint32_t timeout = fire::arg("--timeout", CONFORMANCE_CTS_TIMEOUT_SECONDS)
                                            .bounds(1, static_cast<int>(conformance::driver::MaximumTimeoutSeconds)),
                     uint32_t index = fire::arg("--index", 0), uint32_t count = fire::arg("--count", 1).min(1)) {
  try {
    if (positional.empty()) throw std::invalid_argument("expected run or exec");
    // Fire keeps arguments after -- positional. Its raw argument API preserves
    // launcher flags byte-for-byte without parsing or rewriting the command.
    auto &raw = fire::raw_args;
    auto launch =
        conformance::driver::launcher_arguments(launcher, std::span<char *const>(raw.argv() + 1, static_cast<size_t>(raw.argc() - 1)));
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
      c.ctest = ctest;
      c.workload = workload;
      if (!machine.empty()) c.machine = machine;
      c.selectors = std::move(positional);
      c.launcher = std::move(launch);
      c.isolated_launcher = isolated;
      return conformance::driver::run(std::move(c), conformance::driver::executable_path(raw.argv()[0]));
    }
    if (command == "exec") {
      if (config.empty() || program.empty() || test.empty() || !positional.empty() || launcher)
        throw std::invalid_argument("exec requires --config, --program and --test");
      return conformance::driver::execute_case(config, {test, program, timeout, index, count});
    }
    throw std::invalid_argument("unknown command: " + command);
  } catch (const std::exception &error) {
    std::cerr << "conformance: " << error.what() << '\n';
    return 2;
  }
}
FIRE(conformance_main, "Discover and run Catch executables with CTest, or launch one CTest entry. Launcher syntax: run [options] [targets] "
                       "--launcher -- command [arguments].")
