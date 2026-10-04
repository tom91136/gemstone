#include <algorithm>
#include <array>
#include <atomic>
#include <cerrno>
#include <chrono>
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

#include "driver.hpp"

#if defined(_WIN32)
  #define WIN32_LEAN_AND_MEAN
  #include <windows.h>
#else
  #include <csignal>

  #include <fcntl.h>
  #include <poll.h>
  #include <unistd.h>

  #include <sys/wait.h>

extern "C" char **environ;
#endif

namespace conformance::driver {
namespace {

std::mutex spawn_mutex;

class LineSink {
  std::ostream *stream_;
  std::string prefix_, pending_;
  std::string *capture_;
  std::ostream *log_;
  bool overflow_ = false;
  const std::function<std::string(std::string_view)> &format_line_;

  void emit(std::string_view line) {
    std::osyncstream output(*stream_);
    output << prefix_;
    if (format_line_) output << format_line_(line);
    else output << line;
    output << std::endl;
  }

public:
  LineSink(std::ostream *stream, std::string prefix, std::string *capture, std::ostream *log,
           const std::function<std::string(std::string_view)> &format_line)
      : stream_(stream), prefix_(std::move(prefix)), capture_(capture), log_(log), format_line_(format_line) {}
  void write(const uint8_t *data, size_t size) {
    if (log_) {
      log_->write(reinterpret_cast<const char *>(data), static_cast<std::streamsize>(size));
      log_->flush();
    }
    if (capture_) {
      if (capture_->size() + size > 128 * 1024 * 1024) overflow_ = true;
      else if (!overflow_) capture_->append(reinterpret_cast<const char *>(data), size);
    }
    if (!stream_) return;
    pending_.append(reinterpret_cast<const char *>(data), size);
    size_t begin = 0;
    for (auto end = pending_.find('\n'); end != std::string::npos; end = pending_.find('\n', begin)) {
      emit(std::string_view(pending_).substr(begin, end - begin));
      begin = end + 1;
    }
    pending_.erase(0, begin);
    if (pending_.size() > 16384) finish();
  }
  bool overflow() const { return overflow_; }
  void finish() {
    if (stream_ && !pending_.empty()) {
      emit(pending_);
      pending_.clear();
    }
  }
};

#if defined(_WIN32)
std::wstring widen(std::string_view text) {
  if (text.empty()) return {};
  const auto size = MultiByteToWideChar(CP_UTF8, 0, text.data(), static_cast<int>(text.size()), nullptr, 0);
  std::wstring result(static_cast<size_t>(size), L'\0');
  MultiByteToWideChar(CP_UTF8, 0, text.data(), static_cast<int>(text.size()), result.data(), size);
  return result;
}

// Quote one argument for the lpCommandLine of CreateProcessW.
std::wstring quote_argument(const std::wstring &argument) {
  if (argument.find_first_of(L" \t\"") == std::wstring::npos) return argument;
  std::wstring result = L"\"";
  size_t backslashes = 0;
  for (const auto character : argument) {
    if (character == L'\\') {
      ++backslashes;
      continue;
    }
    if (character == L'"') {
      result.append(backslashes * 2 + 1, L'\\');
      result += L'"';
      backslashes = 0;
      continue;
    }
    result.append(backslashes, L'\\');
    backslashes = 0;
    result += character;
  }
  result.append(backslashes * 2, L'\\');
  result += L'"';
  return result;
}
#else
// The handler only publishes cancellation; each caller owns and reaps its child group.
static_assert(std::atomic<int32_t>::is_always_lock_free);
std::atomic<int32_t> interrupted{0};
std::mutex process_mutex;
size_t active_processes = 0;
struct sigaction old_term = {}, old_int = {};
void forward_signal(int32_t signal) { interrupted.store(signal); }
class ProcessGroup {
  int32_t pid_ = 0;

public:
  ProcessGroup() {
    const std::lock_guard lock(process_mutex);
    if (active_processes++ == 0) {
      interrupted.store(0);
      struct sigaction action = {};
      action.sa_handler = forward_signal;
      sigemptyset(&action.sa_mask);
      sigaction(SIGTERM, &action, &old_term);
      sigaction(SIGINT, &action, &old_int);
    }
  }
  void started(int32_t pid) {
    pid_ = pid;
    ::setpgid(pid, pid);
  }
  ~ProcessGroup() {
    if (pid_ > 0) {
      ::kill(-pid_, SIGKILL);
      while (::waitpid(pid_, nullptr, 0) < 0 && errno == EINTR) {
      }
    }
    const std::lock_guard lock(process_mutex);
    if (--active_processes == 0) {
      sigaction(SIGTERM, &old_term, nullptr);
      sigaction(SIGINT, &old_int, nullptr);
    }
  }
};

// Returns the exit status, -1 on error, or -2 on timeout or cancellation.
int wait_for_exit(int32_t pid, std::chrono::steady_clock::time_point deadline, bool cancellable = true) {
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
#endif

} // namespace

ProcessResult run_process(const std::vector<std::string> &command, const std::filesystem::path &directory,
                          const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout,
                          const std::string &prefix, bool capture_output, std::ostream *output_log,
                          const std::function<std::string(std::string_view)> &format_line) {
  if (command.empty() || timeout.count() <= 0 || timeout.count() > INT32_MAX)
    throw std::invalid_argument("invalid process command or timeout");
  const auto working = directory.string();
  const auto deadline = std::chrono::steady_clock::now() + timeout;

  std::vector<char *> arguments;
  arguments.reserve(command.size() + 1);
  for (const auto &argument : command)
    arguments.push_back(const_cast<char *>(argument.c_str()));
  arguments.push_back(nullptr);

  std::vector<std::string> environment_storage;
  environment_storage.reserve(environment.size());
  for (const auto &[key, value] : environment)
    environment_storage.push_back(key + "=" + value);

  std::string output, error_output;
  LineSink stdout_sink(capture_output || output_log ? nullptr : &std::cout, prefix, capture_output ? &output : nullptr, output_log,
                       format_line);
  LineSink stderr_sink(capture_output || output_log ? nullptr : &std::cerr, prefix, capture_output ? &error_output : nullptr, output_log,
                       format_line);

  const auto completed = [&](int32_t status) {
    if (stdout_sink.overflow() || stderr_sink.overflow())
      throw std::runtime_error("process output exceeded the 128 MiB capture limit: " + command.front());
    return ProcessResult{status, std::move(output), std::move(error_output)};
  };

  const auto timed_out = [&] {
    std::cerr << prefix << "conformance: process tree timed out: " << command.front() << '\n';
    return completed(124);
  };

#if defined(_WIN32)
  std::unique_lock spawn_lock(spawn_mutex);
  SECURITY_ATTRIBUTES attributes{sizeof(SECURITY_ATTRIBUTES), nullptr, TRUE};
  HANDLE output_read = nullptr, output_write = nullptr, error_read = nullptr, error_write = nullptr;
  if (!CreatePipe(&output_read, &output_write, &attributes, 0))
    throw std::runtime_error("cannot create output pipes for " + command.front());
  if (!CreatePipe(&error_read, &error_write, &attributes, 0)) {
    CloseHandle(output_read);
    CloseHandle(output_write);
    throw std::runtime_error("cannot create error pipes for " + command.front());
  }
  SetHandleInformation(output_read, HANDLE_FLAG_INHERIT, 0);
  SetHandleInformation(error_read, HANDLE_FLAG_INHERIT, 0);
  const auto devnull = CreateFileW(L"NUL", GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, &attributes, OPEN_EXISTING, 0, nullptr);

  std::wstring command_line;
  for (const auto &argument : command) {
    if (!command_line.empty()) command_line += L' ';
    command_line += quote_argument(widen(argument));
  }
  std::wstring environment_block;
  for (auto &entry : environment_storage) {
    environment_block.append(widen(entry));
    environment_block.push_back(L'\0');
  }
  if (!environment_storage.empty()) environment_block.push_back(L'\0');

  STARTUPINFOW startup{};
  startup.cb = sizeof(startup);
  startup.dwFlags = STARTF_USESTDHANDLES;
  startup.hStdInput = devnull;
  startup.hStdOutput = output_write;
  startup.hStdError = error_write;
  PROCESS_INFORMATION information{};
  if (!CreateProcessW(nullptr, command_line.data(), nullptr, nullptr, TRUE, CREATE_SUSPENDED | CREATE_UNICODE_ENVIRONMENT,
                      environment_block.empty() ? nullptr : environment_block.data(), working.empty() ? nullptr : widen(working).c_str(),
                      &startup, &information)) {
    CloseHandle(output_read);
    CloseHandle(output_write);
    CloseHandle(error_read);
    CloseHandle(error_write);
    if (devnull != INVALID_HANDLE_VALUE) CloseHandle(devnull);
    throw std::runtime_error("cannot start " + command.front());
  }
  CloseHandle(output_write);
  CloseHandle(error_write);
  if (devnull != INVALID_HANDLE_VALUE) CloseHandle(devnull);

  HANDLE job = CreateJobObjectW(nullptr, nullptr);
  if (job) {
    JOBOBJECT_EXTENDED_LIMIT_INFORMATION limits{};
    limits.BasicLimitInformation.LimitFlags = JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE;
    if (!SetInformationJobObject(job, JobObjectExtendedLimitInformation, &limits, sizeof(limits))
        || !AssignProcessToJobObject(job, information.hProcess)) {
      CloseHandle(job);
      job = nullptr;
    }
  }
  ResumeThread(information.hThread);
  CloseHandle(information.hThread);
  spawn_lock.unlock();

  std::array<uint8_t, 4096> buffer{};
  bool output_open = true, error_open = true;
  bool expired = false;
  while (output_open || error_open) {
    bool progressed = false;
    for (int stream = 0; stream < 2; ++stream) {
      const auto handle = stream == 0 ? output_read : error_read;
      if ((stream == 0 ? output_open : error_open) == false) continue;
      DWORD available = 0;
      if (!PeekNamedPipe(handle, nullptr, 0, nullptr, &available, nullptr)) {
        if (GetLastError() == ERROR_BROKEN_PIPE) {
          CloseHandle(handle);
          (stream == 0 ? output_open : error_open) = false;
          progressed = true;
        }
        continue;
      }
      if (available == 0) continue;
      DWORD count = 0;
      if (ReadFile(handle, buffer.data(), static_cast<DWORD>(std::min<size_t>(available, buffer.size())), &count, nullptr) && count > 0) {
        (stream == 0 ? stdout_sink : stderr_sink).write(buffer.data(), count);
        progressed = true;
      }
    }
    if (std::chrono::steady_clock::now() >= deadline) {
      expired = true;
      break;
    }
    if (!progressed) std::this_thread::sleep_for(std::chrono::milliseconds(5));
  }
  stdout_sink.finish();
  stderr_sink.finish();
  const auto remaining = std::chrono::duration_cast<std::chrono::milliseconds>(deadline - std::chrono::steady_clock::now()).count();
  if (expired || WaitForSingleObject(information.hProcess, static_cast<DWORD>(std::max<int64_t>(remaining, 0))) == WAIT_TIMEOUT) {
    if (job) TerminateJobObject(job, 1);
    TerminateProcess(information.hProcess, 1);
    WaitForSingleObject(information.hProcess, INFINITE);
    DWORD ignored = 0;
    GetExitCodeProcess(information.hProcess, &ignored);
    if (output_open) CloseHandle(output_read);
    if (error_open) CloseHandle(error_read);
    if (job) CloseHandle(job);
    CloseHandle(information.hProcess);
    return timed_out();
  }
  DWORD status = 0;
  GetExitCodeProcess(information.hProcess, &status);
  if (job) CloseHandle(job);
  CloseHandle(information.hProcess);
  return completed(static_cast<int32_t>(status));
#else
  std::vector<char *> environment_pointers;
  for (auto &entry : environment_storage)
    environment_pointers.push_back(entry.data());
  environment_pointers.push_back(nullptr);
  ProcessGroup group;
  // Serialize pipe creation through fork so another child cannot retain our writers.
  std::unique_lock spawn_lock(spawn_mutex);
  int output_pipe[2], error_pipe[2];
  if (::pipe(output_pipe) != 0) throw std::runtime_error("cannot create output pipes for " + command.front());
  if (::pipe(error_pipe) != 0) {
    ::close(output_pipe[0]);
    ::close(output_pipe[1]);
    throw std::runtime_error("cannot create error pipes for " + command.front());
  }

  for (int fd : {output_pipe[0], output_pipe[1], error_pipe[0], error_pipe[1]})
    if (::fcntl(fd, F_SETFD, FD_CLOEXEC) < 0) {
      for (int opened : {output_pipe[0], output_pipe[1], error_pipe[0], error_pipe[1]})
        ::close(opened);
      throw std::runtime_error("cannot protect process pipes for " + command.front());
    }
  const auto pid = ::fork();
  if (pid < 0) {
    ::close(output_pipe[0]);
    ::close(output_pipe[1]);
    ::close(error_pipe[0]);
    ::close(error_pipe[1]);
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
    ::dup2(output_pipe[1], 1);
    ::dup2(error_pipe[1], 2);
    ::close(output_pipe[0]);
    ::close(output_pipe[1]);
    ::close(error_pipe[0]);
    ::close(error_pipe[1]);
    if (::chdir(working.c_str()) != 0) std::_Exit(127);
    environ = environment_pointers.data();
    ::execvp(arguments.front(), arguments.data());
    std::_Exit(127);
  }

  ::close(output_pipe[1]);
  ::close(error_pipe[1]);
  group.started(static_cast<int32_t>(pid));
  spawn_lock.unlock();

  struct pollfd descriptors[2]{{output_pipe[0], POLLIN, 0}, {error_pipe[0], POLLIN, 0}};
  bool open[2]{true, true};
  std::array<uint8_t, 4096> buffer{};
  bool expired = false;
  const auto terminate = [&] {
    ::kill(-pid, SIGTERM);
    ::kill(pid, SIGTERM);
    if (wait_for_exit(pid, std::chrono::steady_clock::now() + std::chrono::seconds(1), false) == -2) {
      ::kill(-pid, SIGKILL);
      ::kill(pid, SIGKILL);
      (void)wait_for_exit(pid, std::chrono::steady_clock::now() + std::chrono::seconds(2), false);
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
        (stream == 0 ? stdout_sink : stderr_sink).write(buffer.data(), static_cast<size_t>(read));
      } else if (read == 0) {
        open[stream] = false;
        ::close(descriptors[stream].fd);
      } else if (errno != EINTR && errno != EAGAIN) {
        open[stream] = false;
        ::close(descriptors[stream].fd);
      }
    }
  }
  stdout_sink.finish();
  stderr_sink.finish();
  if (expired) {
    terminate();
    closeStreams();
    return interrupted.load() ? completed(128 + interrupted.load()) : timed_out();
  }
  const auto status = wait_for_exit(pid, deadline);
  if (status == -2) {
    terminate();
    closeStreams();
    return interrupted.load() ? completed(128 + interrupted.load()) : timed_out();
  }
  if (interrupted.load()) return completed(128 + interrupted.load());
  return completed(static_cast<int32_t>(status));
#endif
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
