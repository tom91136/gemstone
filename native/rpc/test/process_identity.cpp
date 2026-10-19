#include "../process_identity.hpp"

#include <string>

#include <unistd.h>

#include <catch2/catch_test_macros.hpp>

#include <sys/wait.h>

using gemstone::execution::processIncarnation;
using gemstone::execution::detail::processStartTime;

TEST_CASE("process identity distinguishes process incarnations", "[rpc]") {
  REQUIRE((processStartTime("") == 0));
  REQUIRE((processStartTime("42 malformed") == 0));
#ifndef __linux__
  REQUIRE((processIncarnation() == 0));
#else
  // With the PID fixed, different process-start values distinguish PID reuse.
  const std::string prefix = "42 (command with spaces) S 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ";
  REQUIRE((processStartTime(prefix + "12345 0\n") == 12345));
  REQUIRE((processStartTime(prefix + "67890 0\n") == 67890));
  REQUIRE((processStartTime(prefix + "invalid 0\n") == 0));
  const auto start = processIncarnation();
  REQUIRE((start != 0 && start == processIncarnation()));
  const auto parent = getpid();
  const auto child = fork();
  REQUIRE((child >= 0));
  if (child == 0) {
    _exit(getpid() != parent && processIncarnation() != 0 ? 0 : 1);
  }
  int status = 0;
  REQUIRE((waitpid(child, &status, 0) == child));
  REQUIRE((status == 0));
#endif
}
