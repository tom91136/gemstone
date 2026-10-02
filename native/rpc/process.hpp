#ifndef GEMSTONE_EXECUTION_PROCESS_HPP
#define GEMSTONE_EXECUTION_PROCESS_HPP

#include <cstdio>
#include <cstdlib>
#include <filesystem>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <stddef.h>
#include <stdint.h>
#include <unistd.h>

#include <gemstone/file_descriptor.hpp>

#include <sys/types.h>
#include <sys/wait.h>

namespace gemstone::rpc {

namespace detail {
inline std::vector<char> temporaryPattern(std::string_view name) {
  const char *configured = std::getenv("TMPDIR");
  std::string base = configured && *configured ? configured : "/tmp";
  while (base.size() > 1 && base.back() == '/')
    base.pop_back();
  const std::string pattern = base + "/" + std::string(name);
  std::vector<char> buffer(pattern.begin(), pattern.end());
  buffer.push_back('\0');
  return buffer;
}
} // namespace detail

/** Owns a unique temporary directory and removes it on destruction. */
class TemporaryDirectory {
public:
  explicit TemporaryDirectory(std::string_view name) {
    auto buffer = detail::temporaryPattern(name);
    if (const auto created = mkdtemp(buffer.data())) path_ = created;
  }

  ~TemporaryDirectory() {
    std::error_code error;
    std::filesystem::remove_all(path_, error);
  }

  TemporaryDirectory(const TemporaryDirectory &) = delete;
  TemporaryDirectory &operator=(const TemporaryDirectory &) = delete;

  [[nodiscard]] explicit operator bool() const noexcept { return !path_.empty(); }
  [[nodiscard]] std::string file(const std::string &name) const { return (path_ / name).string(); }

private:
  std::filesystem::path path_;
};

/** Owns a unique temporary file and removes it on destruction. */
class TemporaryFile {
public:
  explicit TemporaryFile(std::string_view name = "gemstone-XXXXXX") {
    auto buffer = detail::temporaryPattern(name);
    const int descriptor = mkstemp(buffer.data());
    if (descriptor < 0) return;
    close(descriptor);
    path_ = buffer.data();
  }

  ~TemporaryFile() {
    if (!path_.empty()) unlink(path_.c_str());
  }

  TemporaryFile(TemporaryFile &&other) noexcept : path_(std::exchange(other.path_, {})) {}
  TemporaryFile &operator=(TemporaryFile &&other) noexcept {
    if (this != &other) {
      if (!path_.empty()) unlink(path_.c_str());
      path_ = std::exchange(other.path_, {});
    }
    return *this;
  }
  TemporaryFile(const TemporaryFile &) = delete;
  TemporaryFile &operator=(const TemporaryFile &) = delete;

  [[nodiscard]] bool valid() const noexcept { return !path_.empty(); }
  [[nodiscard]] const std::string &path() const noexcept { return path_; }

  bool write(const void *bytes, size_t size) const {
    FileDescriptor descriptor(open(path_.c_str(), O_WRONLY | O_TRUNC));
    if (descriptor.get() < 0) return false;
    const auto *source = static_cast<const uint8_t *>(bytes);
    size_t written = 0;
    while (written < size) {
      const ssize_t count = ::write(descriptor.get(), source + written, size - written);
      if (count <= 0) return false;
      written += static_cast<size_t>(count);
    }
    return true;
  }

private:
  std::string path_;
};

struct ProcessResult {
  int status = -1;
  std::string output;
};

/** Runs `arguments[0]` with `arguments[1..]`, capturing combined stdout and stderr. A status of 127 means the program
 * could not be executed.
 */
inline ProcessResult runProcess(const std::vector<std::string> &arguments) {
  ProcessResult result;
  if (arguments.empty()) return result;
  int pipeDescriptors[2];
  if (pipe(pipeDescriptors) != 0) return result;
  FileDescriptor readPipe(pipeDescriptors[0]);
  FileDescriptor writePipe(pipeDescriptors[1]);
  std::vector<char *> argv;
  argv.reserve(arguments.size() + 1);
  for (const auto &argument : arguments)
    argv.push_back(const_cast<char *>(argument.c_str()));
  argv.push_back(nullptr);
  const pid_t child = fork();
  if (child == -1) return result;
  if (child == 0) {
    close(readPipe.release());
    if (dup2(writePipe.get(), STDOUT_FILENO) == -1 || dup2(writePipe.get(), STDERR_FILENO) == -1) _exit(126);
    close(writePipe.release());
    execvp(argv[0], argv.data());
    _exit(127);
  }
  close(writePipe.release());
  char buffer[4096];
  for (;;) {
    const ssize_t count = read(readPipe.get(), buffer, sizeof(buffer));
    if (count > 0) result.output.append(buffer, static_cast<size_t>(count));
    else break;
  }
  int status = 0;
  if (waitpid(child, &status, 0) == child) {
    if (WIFEXITED(status)) result.status = WEXITSTATUS(status);
    else if (WIFSIGNALED(status)) result.status = 128 + WTERMSIG(status);
  }
  return result;
}

} // namespace gemstone::rpc

#endif
