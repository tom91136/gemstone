#include <charconv>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <unistd.h>

#include <catch2/catch_session.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/interfaces/catch_interfaces_test_invoker.hpp>
#include <catch2/internal/catch_source_line_info.hpp>
#include <catch2/internal/catch_test_registry.hpp>
#include <catch2/internal/catch_unique_ptr.hpp>

#include "conformance/device.hpp"

int conformance_upstream_main(int argc, const char **argv);
extern int gTestsPassed;

namespace {

int invokeUpstream(std::vector<std::string> arguments) {
  std::vector<const char *> forwarded;
  forwarded.reserve(arguments.size() + 1);
  for (auto &argument : arguments)
    forwarded.push_back(argument.c_str());
  forwarded.push_back(nullptr);
  return conformance_upstream_main(static_cast<int>(arguments.size()), forwarded.data());
}

class StdoutCapture {
public:
  StdoutCapture() : temporary(std::tmpfile()), output(::fileno(stdout)) {
    if (!temporary) throw std::runtime_error("cannot create OpenCL CTS listing buffer");
    std::cout.flush();
    std::fflush(stdout);
    saved = ::dup(output);
    if (saved < 0 || ::dup2(::fileno(temporary), output) < 0) {
      if (saved >= 0) ::close(saved);
      std::fclose(temporary);
      temporary = nullptr;
      throw std::runtime_error("cannot redirect OpenCL CTS listing");
    }
  }

  ~StdoutCapture() {
    if (saved >= 0) {
      std::fflush(stdout);
      ::dup2(saved, output);
      ::close(saved);
    }
    if (temporary) std::fclose(temporary);
  }

  StdoutCapture(const StdoutCapture &) = delete;
  StdoutCapture &operator=(const StdoutCapture &) = delete;

  std::string finish() {
    std::cout.flush();
    std::fflush(stdout);
    const auto restoreStatus = ::dup2(saved, output);
    ::close(saved);
    saved = -1;
    if (restoreStatus < 0) throw std::runtime_error("cannot restore OpenCL CTS output");
    std::rewind(temporary);
    std::string result;
    char buffer[4096];
    while (const auto count = std::fread(buffer, 1, sizeof(buffer), temporary))
      result.append(buffer, count);
    std::fclose(temporary);
    temporary = nullptr;
    return result;
  }

private:
  FILE *temporary;
  int output;
  int saved = -1;
};

std::string listing() {
  StdoutCapture capture;
  const auto status = invokeUpstream({"opencl_cts_" CONFORMANCE_CTS_CATEGORY, "--list"});
  auto result = capture.finish();
  if (status != 0) throw std::runtime_error("OpenCL CTS listing failed");
  return result;
}

std::vector<std::string> cases() {
  const std::string prefix = "cts/opencl/" CONFORMANCE_CTS_CATEGORY "/";
  if (const auto *selected = std::getenv("CONFORMANCE_TEST_IDENTITY"); selected && *selected) {
    const std::string identity(selected);
    if (identity.size() < prefix.size() || identity.compare(0, prefix.size(), prefix) != 0)
      throw std::runtime_error("selected test does not belong to this OpenCL CTS program");
    return {identity.substr(prefix.size())};
  }
  std::vector<std::string> result;
  for (auto remaining = listing(); !remaining.empty();) {
    const auto end = remaining.find('\n');
    auto line = remaining.substr(0, end);
    if (!line.empty() && line.back() == '\r') line.pop_back();
    if (!line.empty() && line.front() == '\t') result.push_back(line.substr(1));
    if (end == std::string::npos) break;
    remaining.erase(0, end + 1);
  }
  return result;
}

class CaseInvoker final : public Catch::ITestInvoker {
public:
  explicit CaseInvoker(std::string name) : name(std::move(name)) {}
  void invoke() const override {
    (void)conformance::deviceSelection();
    std::vector<std::string> arguments{"opencl_cts_" CONFORMANCE_CTS_CATEGORY, name};
    const auto *wimpy = std::getenv("CONFORMANCE_OPENCL_CTS_WIMPY");
    const bool reduced = wimpy && std::string_view(wimpy) == "1";
    if (reduced) arguments.emplace_back("--wimpy");
    if (std::string_view(CONFORMANCE_CTS_CATEGORY) == "compiler" || std::string_view(CONFORMANCE_CTS_CATEGORY) == "spirv_new") {
      const auto *support = std::getenv("CONFORMANCE_SUPPORT_ROOT");
      if (!support || !*support) FAIL("CONFORMANCE_SUPPORT_ROOT is required");
      arguments.emplace_back("--spirv-binaries-path");
      arguments.push_back(std::string(support) + "/data/spirv_bin");
    }
    if (std::string_view(CONFORMANCE_CTS_CATEGORY) == "compiler") {
      const auto *support = std::getenv("CONFORMANCE_SUPPORT_ROOT");
      arguments.emplace_back("--include-test-directory");
      arguments.push_back(std::string(support) + "/data/includeTestDirectory");
      arguments.emplace_back("--second-include-test-directory");
      arguments.push_back(std::string(support) + "/data/secondIncludeTestDirectory");
    }
    if (const auto *elements = std::getenv("CONFORMANCE_OPENCL_CTS_ELEMENTS"); elements && *elements) arguments.emplace_back(elements);
    // Explicit upstream stress controls; unset or empty retains upstream defaults.
    if (std::string_view(CONFORMANCE_CTS_CATEGORY) == "c11_atomics") {
      for (const auto &[key, option] : {std::pair{"CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS", "-internalIterations"},
                                        std::pair{"CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS", "-maxDeviceThreads"}}) {
        if (const auto *value = std::getenv(key); value && *value) {
          const std::string_view text(value);
          int count = 0;
          const auto parsed = std::from_chars(text.data(), text.data() + text.size(), count);
          INFO(key << "=" << value);
          REQUIRE(parsed.ec == std::errc{});
          REQUIRE(parsed.ptr == text.data() + text.size());
          REQUIRE(count > 0);
          arguments.emplace_back(option);
          arguments.emplace_back(value);
        }
      }
    }
    const auto passedBefore = gTestsPassed;
    REQUIRE(invokeUpstream(std::move(arguments)) == 0);
    if (gTestsPassed == passedBefore) SKIP("OpenCL CTS skipped this test for the selected device");
  }

private:
  std::string name;
};

void registerCases() {
  for (auto &name : cases()) {
    const auto identity = "cts/opencl/" CONFORMANCE_CTS_CATEGORY "/" + name;
    Catch::AutoReg(Catch::Detail::make_unique<CaseInvoker>(std::move(name)), CATCH_INTERNAL_LINEINFO, Catch::StringRef(),
                   Catch::NameAndTags{identity, "[cts][opencl]"});
  }
}

} // namespace

#ifdef main
  #undef main
#endif
int main(int argc, char **argv) {
  registerCases();
  return Catch::Session().run(argc, argv);
}
