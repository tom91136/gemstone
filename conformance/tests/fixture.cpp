#include <chrono>
#include <csignal>
#include <cstdlib>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <string>
#include <string_view>
#include <thread>

#include <unistd.h>

#include <catch2/catch_session.hpp>
#include <catch2/catch_test_macros.hpp>

#include <conformance/environment_keys.hpp>

TEST_CASE("a comma, bracket[quoted] star* backslash\\ semi; quote\" unicode-π", "[fixture][pass]") {
  if (const auto *report = std::getenv(conformance::environment::ReportDir))
    std::ofstream(std::filesystem::path(report) / "result.txt") << "pass\n";
  const auto *expected = std::getenv("EXPECTED_ENVIRONMENT");
  if (expected) {
    const auto *value = std::getenv("PROPAGATED_ENVIRONMENT");
    REQUIRE(value);
    REQUIRE(std::string(value) == expected);
  }
  const auto *architecture = std::getenv(conformance::environment::TargetArch);
  REQUIRE((architecture == nullptr || std::string_view(architecture).empty()));
}

TEST_CASE("z skipped", "[fixture][skip]") { SKIP("native skip fixture"); }

TEST_CASE("not selected failure", "[failure]") { FAIL("wrong shard or spec selected"); }

TEST_CASE("noisy failure", "[noisy]") {
  std::cout << "first diagnostic\n";
  for (int i = 0; i < 5000; ++i)
    std::cout << "progress line\n";
  std::cout << "last diagnostic\n";
  std::cerr << "stderr diagnostic\n";
  FAIL("expected noisy failure");
}

TEST_CASE("hung case", "[hang]") {
  const auto child = fork();
  if (child == 0) {
    std::signal(SIGTERM, SIG_IGN);
    for (;;)
      pause();
  }
  REQUIRE(child > 0);
  std::signal(SIGTERM, SIG_IGN);
  const auto *report = std::getenv(conformance::environment::ReportDir);
  REQUIRE(report);
  const auto pids = std::filesystem::path(report) / "hang.pids";
  std::ofstream(pids.string() + ".partial") << getppid() << ' ' << getpid() << ' ' << child;
  std::filesystem::rename(pids.string() + ".partial", pids);
  for (;;)
    pause();
}

int main(int argc, char **argv) {
  if (argc > 2 && std::string_view(argv[1]).starts_with("--deqp-case=")) {
    const auto identity = std::string(argv[1]).substr(std::string_view("--deqp-case=").size());
    const auto log = std::string(argv[2]).substr(std::string_view("--deqp-log-filename=").size());
    std::ofstream(log) << "#beginTestCaseResult " << identity << "\n<TestCaseResult CasePath=\"" << identity
                       << R"(" Version="0.3.4"><Result StatusCode=")" << (identity.ends_with(".fail") ? "Fail" : "Pass")
                       << "\">fixture</Result></TestCaseResult>\n#endTestCaseResult\n";
    std::cout << "fixture dEQP output\n";
    return 0;
  }
  if (argc > 1 && std::string_view(argv[1]) == "--large-inventory") {
    const std::string group = "GROUP: " + std::string(1024 - 8, 'x') + "\n";
    for (size_t i = 0; i < size_t{129} * 1024; ++i)
      std::cout << group;
    std::cout << "TEST: dEQP-VK.last.case\n";
    return 0;
  }
  if (argc > 1 && std::string_view(argv[1]) == "--process-tree") {
    std::signal(SIGTERM, SIG_IGN);
    const auto child = fork();
    if (child == 0) {
      std::signal(SIGTERM, SIG_IGN);
      for (;;)
        pause();
    }
    if (child < 0) return 2;
    std::cout << child << '\n' << std::flush;
    for (;;)
      pause();
  }
  if (std::filesystem::path(argv[0]).stem() == "conformance-device") {
    if (std::filesystem::exists("probe-barrier")) {
      std::ofstream("probe-pids") << getppid() << ' ' << getpid();
      std::ofstream("probe-ready") << "ready";
      const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(10);
      while (!std::filesystem::exists("probe-release")) {
        if (std::chrono::steady_clock::now() >= deadline) return 3;
        std::this_thread::sleep_for(std::chrono::milliseconds(5));
      }
    }
    if (std::filesystem::exists("probe-failure")) return 7;
    std::cout << R"({"device":"pci-0123456789abcdef0123456789abcdef","environment":{"CL_DEVICE_INDEX":"0"}})";
    return 0;
  }
  return Catch::Session().run(argc, argv);
}
