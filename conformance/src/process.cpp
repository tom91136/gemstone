#include <cstdlib>
#include <iostream>
#include <stdexcept>
#include <syncstream>
#include <utility>

#include <reproc++/drain.hpp>
#include <reproc++/reproc.hpp>

#include "conformance/driver.hpp"
#if defined(_WIN32)
  #include <windows.h>
#elif defined(__APPLE__)
  #include <csignal>

  #include <unistd.h>

  #include <mach-o/dyld.h>
#else
  #include <csignal>

  #include <unistd.h>
#endif

namespace conformance::driver {
namespace {
#if !defined(_WIN32)
volatile sig_atomic_t active_group = 0;
volatile sig_atomic_t interrupted = 0;
void forward_signal(int32_t signal) {
  interrupted = signal;
  if (active_group > 0) ::kill(-active_group, signal);
}
class ProcessGroup {
  int32_t pid_;
  struct sigaction old_term_{}, old_int_{};

public:
  explicit ProcessGroup(int32_t pid) : pid_(pid) {
    interrupted = 0;
    active_group = pid;
    struct sigaction action{};
    action.sa_handler = forward_signal;
    sigemptyset(&action.sa_mask);
    sigaction(SIGTERM, &action, &old_term_);
    sigaction(SIGINT, &action, &old_int_);
  }
  ~ProcessGroup() {
    // Also reap a group's surviving workers when its leader exits successfully.
    ::kill(-pid_, SIGKILL);
    active_group = 0;
    sigaction(SIGTERM, &old_term_, nullptr);
    sigaction(SIGINT, &old_int_, nullptr);
  }
};
#endif
class LineSink {
  std::ostream *stream_;
  std::string prefix_, pending_;
  std::string *capture_;

public:
  LineSink(std::ostream *stream, std::string prefix, std::string *capture = nullptr)
      : stream_(stream), prefix_(std::move(prefix)), capture_(capture) {}
  std::error_code operator()(reproc::stream, const uint8_t *data, size_t size) {
    if (capture_) {
      if (capture_->size() + size > 128 * 1024 * 1024) return std::make_error_code(std::errc::file_too_large);
      capture_->append(reinterpret_cast<const char *>(data), size);
    }
    if (!stream_) return {};
    pending_.append(reinterpret_cast<const char *>(data), size);
    size_t begin = 0;
    for (auto end = pending_.find('\n'); end != std::string::npos; end = pending_.find('\n', begin)) {
      std::osyncstream(*stream_) << prefix_ << std::string_view(pending_).substr(begin, end - begin) << '\n';
      begin = end + 1;
    }
    pending_.erase(0, begin);
    if (pending_.size() > 16384) finish();
    return {};
  }
  void finish() {
    if (stream_ && !pending_.empty()) {
      std::osyncstream(*stream_) << prefix_ << pending_ << '\n';
      pending_.clear();
    }
  }
};
} // namespace
RunResult run_process(const std::vector<std::string> &command, const std::filesystem::path &directory,
                      const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout, const std::string &prefix,
                      bool capture_output) {
  if (command.empty() || timeout.count() <= 0 || timeout.count() > INT32_MAX)
    throw std::invalid_argument("invalid process command or timeout");
  const auto working = directory.string();
  reproc::options options;
  options.working_directory = working.c_str();
  options.env.behavior = reproc::env::empty;
  options.env.extra = environment;
  options.deadline = reproc::milliseconds(static_cast<int32_t>(timeout.count()));
  options.nonblocking = true;
  options.redirect.in.type = reproc::redirect::discard;
  options.redirect.out.type = reproc::redirect::pipe;
  options.redirect.err.type = reproc::redirect::pipe;
  options.stop = {{reproc::stop::terminate, reproc::milliseconds(1000)}, {reproc::stop::kill, reproc::milliseconds(2000)}, {}};
  reproc::process process;
  if (const auto error = process.start(command, options); error)
    throw std::runtime_error("cannot start " + command.front() + ": " + error.message());
#if !defined(_WIN32)
  const auto [pid, pid_error] = process.pid();
  if (pid_error) throw std::runtime_error("cannot obtain child process group");
  ProcessGroup group(pid);
#endif
  std::string output, error_output;
  LineSink stdout_sink(capture_output ? nullptr : &std::cout, prefix, capture_output ? &output : nullptr);
  LineSink stderr_sink(capture_output ? nullptr : &std::cerr, prefix, capture_output ? &error_output : nullptr);
  const auto error = reproc::drain(process, stdout_sink, stderr_sink);
  stdout_sink.finish();
  stderr_sink.finish();
  if (error) {
    (void)process.stop(options.stop);
    if (error == std::errc::timed_out) {
      std::cerr << prefix << "conformance: process tree timed out: " << command.front() << '\n';
      return {124, std::move(output), std::move(error_output)};
    }
    throw std::runtime_error("cannot read " + command.front() + ": " + error.message());
  }
  const auto [status, wait_error] = process.wait(reproc::deadline);
  if (wait_error) {
    (void)process.stop(options.stop);
    if (wait_error == std::errc::timed_out) return {124, std::move(output), std::move(error_output)};
    throw std::runtime_error("cannot wait for " + command.front() + ": " + wait_error.message());
  }
#if !defined(_WIN32)
  if (interrupted) return {128 + interrupted, std::move(output), std::move(error_output)};
#endif
  return {status, std::move(output), std::move(error_output)};
}
std::filesystem::path executable_path(const char *argument) {
#if defined(_WIN32)
  std::wstring buffer(32768, L'\0');
  const auto size = GetModuleFileNameW(nullptr, buffer.data(), static_cast<DWORD>(buffer.size()));
  if (size > 0 && size < buffer.size()) {
    buffer.resize(size);
    return buffer;
  }
#elif defined(__APPLE__)
  uint32_t size = 0;
  _NSGetExecutablePath(nullptr, &size);
  std::string buffer(size, '\0');
  if (_NSGetExecutablePath(buffer.data(), &size) == 0) return std::filesystem::weakly_canonical(buffer.c_str());
#else
  std::error_code error;
  const auto image = std::filesystem::read_symlink("/proc/self/exe", error);
  if (!error) return image;
#endif
  return std::filesystem::weakly_canonical(std::filesystem::absolute(argument));
}
} // namespace conformance::driver
