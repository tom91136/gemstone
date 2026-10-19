#pragma once

#include <cstdio>
#include <cstdlib>
#include <filesystem>
#include <string>
#include <string_view>
#include <vector>

#include <fcntl.h>
#include <stddef.h>
#include <stdint.h>
#include <unistd.h>

#include <gemstone/file_descriptor.hpp>

#include <sys/types.h>
#include <sys/wait.h>

namespace gemstone {

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

/** Creates or truncates `path` and writes all of `bytes`. */
inline bool writeFile(const std::string &path, const void *bytes, size_t size) {
  FileDescriptor descriptor(open(path.c_str(), O_WRONLY | O_CREAT | O_TRUNC, 0600));
  if (descriptor.get() < 0) return false;
  const auto *source = static_cast<const uint8_t *>(bytes);
  for (size_t written = 0; written < size;) {
    const ssize_t count = ::write(descriptor.get(), source + written, size - written);
    if (count <= 0) return false;
    written += static_cast<size_t>(count);
  }
  return close(descriptor.release()) == 0;
}

/** Owns a unique temporary directory and removes it on destruction. */
class TemporaryDirectory {
public:
  explicit TemporaryDirectory(std::string_view name) {
    auto buffer = detail::temporaryPattern(name);
    if (auto *const created = mkdtemp(buffer.data())) path = created;
  }

  ~TemporaryDirectory() {
    std::error_code error;
    std::filesystem::remove_all(path, error);
  }

  TemporaryDirectory(const TemporaryDirectory &) = delete;
  TemporaryDirectory &operator=(const TemporaryDirectory &) = delete;

  [[nodiscard]] explicit operator bool() const noexcept { return !path.empty(); }
  [[nodiscard]] std::string file(const std::string &name) const { return (path / name).string(); }

private:
  std::filesystem::path path;
};

struct ProcessResult {
  int status = -1;
  std::string output;
};

/** Runs `arguments[0]` with `arguments[1..]`, capturing combined stdout and stderr. A status of 127 means the program
 * could not be executed.
 */
inline ProcessResult runProcess(std::vector<std::string> arguments) {
  ProcessResult result;
  if (arguments.empty()) return result;
  int pipeDescriptors[2];
  if (pipe(pipeDescriptors) != 0) return result;
  FileDescriptor readPipe(pipeDescriptors[0]);
  FileDescriptor writePipe(pipeDescriptors[1]);
  std::vector<char *> argv;
  argv.reserve(arguments.size() + 1);
  for (auto &argument : arguments)
    argv.push_back(argument.data());
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

} // namespace gemstone
