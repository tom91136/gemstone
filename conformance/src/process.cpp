#include <algorithm>
#include <array>
#include <atomic>
#include <cerrno>
#include <chrono>
#include <csignal>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <mutex>
#include <stdexcept>
#include <string>
#include <string_view>
#include <syncstream>
#include <thread>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <poll.h>
#include <unistd.h>

#include "driver.hpp"
#include <fmt/format.h>
#include <sys/wait.h>

extern "C" char **environ;

namespace conformance::driver {
namespace {

std::mutex spawnMutex;

class LineSink {
  std::ostream *stream;
  std::string prefix, pending;
  std::string *capture;
  std::ostream *log;

  void emit(std::string_view line) { std::osyncstream(*stream) << prefix << line << std::endl; }

public:
  bool overflow = false;

  LineSink(std::ostream *stream, std::string prefix, std::string *capture, std::ostream *log)
      : stream(stream), prefix(std::move(prefix)), capture(capture), log(log) {}
  void write(const uint8_t *data, size_t size) {
    if (log) {
      log->write(reinterpret_cast<const char *>(data), static_cast<std::streamsize>(size));
      log->flush();
    }
    if (capture) {
      if (capture->size() + size > 128 * 1024 * 1024) overflow = true;
      else if (!overflow) capture->append(reinterpret_cast<const char *>(data), size);
    }
    if (!stream) return;
    pending.append(reinterpret_cast<const char *>(data), size);
    size_t begin = 0;
    for (auto end = pending.find('\n'); end != std::string::npos; end = pending.find('\n', begin)) {
      emit(std::string_view(pending).substr(begin, end - begin));
      begin = end + 1;
    }
    pending.erase(0, begin);
    if (pending.size() > 16384) finish();
  }
  void finish() {
    if (stream && !pending.empty()) {
      emit(pending);
      pending.clear();
    }
  }
};

// The handler only publishes cancellation; each caller owns and reaps its child group.
static_assert(std::atomic<int32_t>::is_always_lock_free);
std::atomic<int32_t> interrupted{0};
std::mutex interruptionMutex;
size_t interruptionScopes = 0;
struct sigaction oldTerm = {}, oldInt = {};
void forwardSignal(int32_t signal) { interrupted.store(signal); }
class ProcessGroup {
  Interruption interruption;
  int32_t leader = 0;

public:
  void started(int32_t pid) {
    leader = pid;
    ::setpgid(pid, pid);
  }
  ~ProcessGroup() {
    if (leader > 0) {
      ::kill(-leader, SIGKILL);
      while (::waitpid(leader, nullptr, 0) < 0 && errno == EINTR) {
      }
    }
  }
};

// Returns the exit status, -1 on error, or -2 on timeout or cancellation.
int waitForExit(int32_t pid, std::chrono::steady_clock::time_point deadline, bool cancellable = true) {
  for (;;) {
    int status = 0;
    const auto result = ::waitpid(pid, &status, WNOHANG);
    if (result == pid) {
      if (WIFEXITED(status)) return WEXITSTATUS(status);
      if (WIFSIGNALED(status)) return 128 + WTERMSIG(status);
      return 0;
    }
    if (result < 0) {
      if (errno == EINTR) continue;
      return -1;
    }
    if (std::chrono::steady_clock::now() >= deadline || (cancellable && interrupted.load())) return -2;
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
  }
}

} // namespace

Interruption::Interruption() {
  const std::lock_guard lock(interruptionMutex);
  if (interruptionScopes++ == 0) {
    interrupted.store(0);
    struct sigaction action = {};
    action.sa_handler = forwardSignal;
    sigemptyset(&action.sa_mask);
    sigaction(SIGTERM, &action, &oldTerm);
    sigaction(SIGINT, &action, &oldInt);
  }
}
Interruption::~Interruption() {
  const std::lock_guard lock(interruptionMutex);
  if (--interruptionScopes == 0) {
    sigaction(SIGTERM, &oldTerm, nullptr);
    sigaction(SIGINT, &oldInt, nullptr);
  }
}
int32_t Interruption::signal() { return interrupted.load(); }

ProcessResult runProcess(const std::vector<std::string> &command, const std::filesystem::path &directory,
                         const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout,
                         const std::string &prefix, bool captureOutput, std::ostream *outputLog) {
  if (command.empty() || timeout.count() <= 0 || timeout.count() > INT32_MAX)
    throw std::invalid_argument("invalid process command or timeout");
  const auto working = directory.string();
  const auto deadline = std::chrono::steady_clock::now() + timeout;

  std::vector<char *> arguments;
  arguments.reserve(command.size() + 1);
  for (const auto &argument : command)
    arguments.push_back(const_cast<char *>(argument.c_str()));
  arguments.push_back(nullptr);

  std::vector<std::string> environmentStorage;
  environmentStorage.reserve(environment.size());
  for (const auto &[key, value] : environment)
    environmentStorage.push_back(key + "=" + value);

  std::string output, errorOutput;
  LineSink stdoutSink(captureOutput || outputLog ? nullptr : &std::cout, prefix, captureOutput ? &output : nullptr, outputLog);
  LineSink stderrSink(captureOutput || outputLog ? nullptr : &std::cerr, prefix, captureOutput ? &errorOutput : nullptr, outputLog);

  const auto completed = [&](int32_t status, bool timedOut = false) {
    if (stdoutSink.overflow || stderrSink.overflow)
      throw std::runtime_error("process output exceeded the 128 MiB capture limit: " + command.front());
    return ProcessResult{status, std::move(output), std::move(errorOutput), timedOut};
  };

  const auto timedOut = [&] {
    const auto message = fmt::format("conformance: process tree timed out after {:g} s: {}\n",
                                     std::chrono::duration<double>(timeout).count(), command.front());
    if (outputLog) *outputLog << message << std::flush;
    else std::cerr << prefix << message;
    return completed(124, true);
  };

  std::vector<char *> environmentPointers;
  for (auto &entry : environmentStorage)
    environmentPointers.push_back(entry.data());
  environmentPointers.push_back(nullptr);
  ProcessGroup group;
  // Serialise pipe creation through fork so another child cannot retain our writers.
  std::unique_lock spawnLock(spawnMutex);
  int outputPipe[2], errorPipe[2];
  if (::pipe(outputPipe) != 0) throw std::runtime_error("cannot create output pipes for " + command.front());
  if (::pipe(errorPipe) != 0) {
    ::close(outputPipe[0]);
    ::close(outputPipe[1]);
    throw std::runtime_error("cannot create error pipes for " + command.front());
  }

  for (int fd : {outputPipe[0], outputPipe[1], errorPipe[0], errorPipe[1]})
    if (::fcntl(fd, F_SETFD, FD_CLOEXEC) < 0) {
      for (int opened : {outputPipe[0], outputPipe[1], errorPipe[0], errorPipe[1]})
        ::close(opened);
      throw std::runtime_error("cannot protect process pipes for " + command.front());
    }
  const auto pid = ::fork();
  if (pid < 0) {
    ::close(outputPipe[0]);
    ::close(outputPipe[1]);
    ::close(errorPipe[0]);
    ::close(errorPipe[1]);
    throw std::runtime_error("cannot start " + command.front());
  }
  if (pid == 0) {
    // Join a fresh process group so timeout cleanup reaches every descendant.
    ::setpgid(0, 0);
    const int devnull = ::open("/dev/null", O_RDONLY);
    if (devnull >= 0) {
      ::dup2(devnull, 0);
      if (devnull > 2) ::close(devnull);
    }
    ::dup2(outputPipe[1], 1);
    ::dup2(errorPipe[1], 2);
    ::close(outputPipe[0]);
    ::close(outputPipe[1]);
    ::close(errorPipe[0]);
    ::close(errorPipe[1]);
    if (::chdir(working.c_str()) != 0) std::_Exit(127);
    environ = environmentPointers.data();
    ::execvp(arguments.front(), arguments.data());
    std::_Exit(127);
  }

  ::close(outputPipe[1]);
  ::close(errorPipe[1]);
  group.started(static_cast<int32_t>(pid));
  spawnLock.unlock();

  struct pollfd descriptors[2]{{outputPipe[0], POLLIN, 0}, {errorPipe[0], POLLIN, 0}};
  bool open[2]{true, true};
  std::array<uint8_t, 4096> buffer{};
  bool expired = false;
  const auto terminate = [&] {
    ::kill(-pid, SIGTERM);
    ::kill(pid, SIGTERM);
    if (waitForExit(pid, std::chrono::steady_clock::now() + std::chrono::seconds(1), false) == -2) {
      ::kill(-pid, SIGKILL);
      ::kill(pid, SIGKILL);
      (void)waitForExit(pid, std::chrono::steady_clock::now() + std::chrono::seconds(2), false);
    }
  };
  const auto closeStreams = [&] {
    for (int stream = 0; stream < 2; ++stream)
      if (open[stream]) {
        open[stream] = false;
        ::close(descriptors[stream].fd);
      }
  };
  while (open[0] || open[1]) {
    const auto now = std::chrono::steady_clock::now();
    if (now >= deadline || interrupted.load()) {
      expired = true;
      break;
    }
    struct pollfd active[2];
    int indices[2];
    int count = 0;
    for (int stream = 0; stream < 2; ++stream) {
      if (!open[stream]) continue;
      active[count] = descriptors[stream];
      indices[count] = stream;
      ++count;
    }
    const auto remaining = std::chrono::duration_cast<std::chrono::milliseconds>(deadline - now).count();
    const auto ready = ::poll(active, static_cast<nfds_t>(count), static_cast<int>(std::min<int64_t>(remaining, 100)));
    if (ready < 0) {
      if (errno == EINTR) continue;
      closeStreams();
      throw std::runtime_error("cannot read " + command.front() + ": " + std::strerror(errno));
    }
    if (ready == 0) continue;
    for (int slot = 0; slot < count; ++slot) {
      if ((active[slot].revents & (POLLIN | POLLHUP | POLLERR)) == 0) continue;
      const int stream = indices[slot];
      const auto read = ::read(descriptors[stream].fd, buffer.data(), buffer.size());
      if (read > 0) {
        (stream == 0 ? stdoutSink : stderrSink).write(buffer.data(), static_cast<size_t>(read));
      } else if (read == 0) {
        open[stream] = false;
        ::close(descriptors[stream].fd);
      } else if (errno != EINTR && errno != EAGAIN) {
        open[stream] = false;
        ::close(descriptors[stream].fd);
      }
    }
  }
  stdoutSink.finish();
  stderrSink.finish();
  const auto status = expired ? -2 : waitForExit(pid, deadline);
  if (status == -2) {
    terminate();
    closeStreams();
  }
  if (interrupted.load()) return completed(128 + interrupted.load());
  return status == -2 ? timedOut() : completed(static_cast<int32_t>(status));
}

} // namespace conformance::driver
