#pragma once

#include <chrono>
#include <string>
#include <string_view>

#include <fcntl.h>
#include <poll.h>
#include <signal.h>
#include <unistd.h>

#include <gemstone/test_support.hpp>

#include <sys/resource.h>
#include <sys/wait.h>

namespace gemstone_test {

struct ChildOutcome {
  int signal = 0;
  int exitCode = -1;
  std::string output;
};

// Runs one hidden test case of this executable in a fresh process, so a test can expect the process to die.
inline ChildOutcome runChildTest(std::string_view name) {
  int pipes[2];
  if (pipe2(pipes, O_CLOEXEC) != 0) return {};
  const std::string testName(name);
  const pid_t child = fork();
  if (child < 0) return {};
  if (child == 0) {
    const rlimit noCore{.rlim_cur = 0, .rlim_max = 0};
    setrlimit(RLIMIT_CORE, &noCore);
    dup2(pipes[1], STDOUT_FILENO);
    dup2(pipes[1], STDERR_FILENO);
    execl("/proc/self/exe", "/proc/self/exe", testName.c_str(), static_cast<char *>(nullptr));
    _exit(127);
  }
  close(pipes[1]);
  ChildOutcome outcome;
  const auto deadline = std::chrono::steady_clock::now() + scaled(std::chrono::seconds(120));
  char buffer[4096];
  for (;;) {
    const auto remaining = std::chrono::duration_cast<std::chrono::milliseconds>(deadline - std::chrono::steady_clock::now());
    pollfd watched{.fd = pipes[0], .events = POLLIN, .revents = 0};
    if (remaining.count() <= 0 || poll(&watched, 1, static_cast<int>(remaining.count())) <= 0) {
      kill(child, SIGKILL);
      break;
    }
    const auto count = read(pipes[0], buffer, sizeof(buffer));
    if (count <= 0) break;
    outcome.output.append(buffer, static_cast<size_t>(count));
  }
  close(pipes[0]);
  int status = 0;
  if (waitpid(child, &status, 0) != child) return outcome;
  if (WIFSIGNALED(status)) outcome.signal = WTERMSIG(status);
  if (WIFEXITED(status)) outcome.exitCode = WEXITSTATUS(status);
  return outcome;
}

} // namespace gemstone_test
