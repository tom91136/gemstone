#include <cstdlib>
#include <exception>
#include <filesystem>
#include <iostream>
#include <sstream>
#include <string>
#include <string_view>
#include <vector>

#include <spawn.h>
#include <unistd.h>

#include <catch2/catch_session.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/interfaces/catch_interfaces_test_invoker.hpp>
#include <catch2/internal/catch_source_line_info.hpp>
#include <catch2/internal/catch_test_registry.hpp>
#include <catch2/internal/catch_unique_ptr.hpp>

#include "conformance/device.hpp"

#include <sys/wait.h>

extern char **environ;

namespace {

namespace fs = std::filesystem;

constexpr int ExitWaived = 2;

struct Row {
  std::string_view name, executable, unsupported, arguments, expected;
};

constexpr Row Rows[] = {
#include "cases.inc"
};

struct Run {
  int status = -1;
  std::string output;
};

Run run(const fs::path &executable, std::string_view extra) {
  const auto path = executable.string();
  const auto directory = executable.parent_path().string();
  std::vector<std::string> words{path};
  std::istringstream stream{std::string(extra)};
  for (std::string word; stream >> word;)
    words.push_back(word);
  std::vector<char *> arguments;
  for (auto &word : words)
    arguments.push_back(word.data());
  arguments.push_back(nullptr);
  int pipes[2];
  if (pipe(pipes) != 0) return {};
  posix_spawn_file_actions_t actions;
  posix_spawn_file_actions_init(&actions);
  posix_spawn_file_actions_addchdir_np(&actions, directory.c_str());
  posix_spawn_file_actions_adddup2(&actions, pipes[1], STDOUT_FILENO);
  posix_spawn_file_actions_addclose(&actions, pipes[0]);
  posix_spawn_file_actions_addclose(&actions, pipes[1]);
  pid_t child = 0;
  const auto error = posix_spawn(&child, path.c_str(), &actions, nullptr, arguments.data(), environ);
  posix_spawn_file_actions_destroy(&actions);
  close(pipes[1]);
  Run result;
  if (error == 0) {
    char chunk[4096];
    while (const auto count = read(pipes[0], chunk, sizeof(chunk))) {
      if (count < 0) break;
      result.output.append(chunk, static_cast<size_t>(count));
    }
  }
  close(pipes[0]);
  if (error != 0) return result;
  int status = 0;
  if (waitpid(child, &status, 0) < 0) return result;
  result.status = WIFEXITED(status) ? WEXITSTATUS(status) : 128 + WTERMSIG(status);
  return result;
}

std::vector<std::string> missingLines(std::string_view output, std::string_view expected) {
  std::vector<std::string> missing;
  size_t begin = 0;
  while (begin <= expected.size()) {
    auto end = expected.find('\n', begin);
    if (end == std::string_view::npos) end = expected.size();
    const std::string line(expected.substr(begin, end - begin));
    if (!line.empty() && output.find(line) == std::string_view::npos) missing.push_back(line);
    begin = end + 1;
  }
  return missing;
}

class ExampleInvoker final : public Catch::ITestInvoker {
public:
  explicit ExampleInvoker(const Row &row) : row(row) {}
  void invoke() const override {
    if (!row.unsupported.empty()) SKIP(std::string(row.unsupported));
    std::cout.flush();
    const auto executable = fs::read_symlink("/proc/self/exe").parent_path() / row.executable;
    INFO(executable.string());
    const auto result = run(executable, row.arguments);
    std::cout << result.output << std::flush;
    if (result.status == ExitWaived) SKIP("requires two devices");
    REQUIRE(result.status == 0);
    const auto missing = missingLines(result.output, row.expected);
    INFO("expected output lines missing: " << missing.size());
    for (const auto &line : missing)
      UNSCOPED_INFO("missing: " << line);
    REQUIRE(missing.empty());
  }

private:
  const Row &row;
};

} // namespace

int main(int argc, char **argv) {
  for (const auto &row : Rows)
    Catch::AutoReg(Catch::Detail::make_unique<ExampleInvoker>(row), CATCH_INTERNAL_LINEINFO, Catch::StringRef(),
                   Catch::NameAndTags{std::string(row.name), "[vendor][hip]"});
  try {
    Catch::Session session;
    if (const auto status = session.applyCommandLine(argc, argv)) return status;
    const auto &config = session.configData();
    if (!config.listTests && !config.listTags && !config.listReporters && !config.listListeners && !config.showHelp && !config.libIdentify)
      (void)conformance::deviceSelection();
    return session.run();
  } catch (const std::exception &error) {
    std::cerr << "vendor_hip: " << error.what() << '\n';
    return 2;
  }
}
