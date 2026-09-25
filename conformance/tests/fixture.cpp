#include <chrono>
#include <cstdlib>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <string>
#include <thread>

#include <catch2/catch_session.hpp>
#include <catch2/catch_test_macros.hpp>
#if !defined(_WIN32)
  #include <csignal>

  #include <unistd.h>
#endif

TEST_CASE("a comma, bracket[quoted] star* backslash\\ semi; quote\" unicode-π", "[fixture][pass]") {
  if (const auto *report = std::getenv("CONFORMANCE_REPORT_DIRECTORY"))
    std::ofstream(std::filesystem::path(report) / "result.txt") << "pass\n";
  const auto *expected = std::getenv("EXPECTED_ENVIRONMENT");
  if (expected) {
    const auto *value = std::getenv("PROPAGATED_ENVIRONMENT");
    REQUIRE(value);
    REQUIRE(std::string(value) == expected);
  }
  REQUIRE(
      (std::getenv("CONFORMANCE_TARGET_ARCHITECTURE") == nullptr || std::string(std::getenv("CONFORMANCE_TARGET_ARCHITECTURE")).empty()));
}

TEST_CASE("z skipped", "[fixture][skip]") { SKIP("native skip fixture"); }

TEST_CASE("not selected failure", "[failure]") { FAIL("wrong shard or spec selected"); }

int main(int argc, char **argv) {
  if (argc > 1 && std::string_view(argv[1]) == "--process-tree") {
#if !defined(_WIN32)
    std::signal(SIGTERM, SIG_IGN);
    const auto child = fork();
    if (child == 0) {
      std::signal(SIGTERM, SIG_IGN);
      for (;;)
        pause();
    }
    if (child < 0) return 2;
    std::cout << child << std::endl;
    for (;;)
      pause();
#else
    return 0;
#endif
  }
  return Catch::Session().run(argc, argv);
}
