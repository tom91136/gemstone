#include <algorithm>
#include <array>
#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <filesystem>
#include <map>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <unistd.h>

#include <catch2/catch_session.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/interfaces/catch_interfaces_test_invoker.hpp>
#include <catch2/internal/catch_source_line_info.hpp>
#include <catch2/internal/catch_test_registry.hpp>
#include <catch2/internal/catch_unique_ptr.hpp>

#include "conformance/device.hpp"

#include "vendor_cases.inc"
#include <sys/wait.h>

namespace {

constexpr int ExitWaived = 2;

// some samples report a failed self-check only in their output and still exit 0
constexpr std::array<std::string_view, 6> FailureMarkers{"Test failed",    "Test FAILED", "Result = FAIL",
                                                         "Example FAILED", "[FAILED]",    "Atomics FAILED"};

std::string executable;

using Lines = std::vector<std::string>;

Lines simplePrintfLines() {
  Lines lines;
  for (int block = 0; block < 4; ++block)
    for (int thread = 0; thread < 8; ++thread)
      lines.push_back("[" + std::to_string(block) + ", " + std::to_string(thread) + "]:\t\tValue is:10");
  return lines;
}

Lines cooperativeGroupsLines() {
  Lines lines{" Sum of all ranks 0..63 in threadBlockGroup is 2016 (expected 2016)"};
  for (int group = 0; group < 4; ++group)
    lines.push_back("   Sum of all ranks 0..15 in this tiledPartition16 group is 120 (expected 120)");
  return lines;
}

Lines atomicRefSmallLines() { return {"expected: 0x0\r", "result:   0x0\r", "expected: 0x2000200\r", "result:   0x2000200\r"}; }

// device printf has no host-visible effect besides the output, so a dropped line would otherwise pass vacuously
const std::map<std::string_view, Lines (*)()> ExpectedLines{
    {"vendor/cuda-samples/simplePrintf", simplePrintfLines},
    {"vendor/cuda-samples/simpleCooperativeGroups", cooperativeGroupsLines},
    {"vendor/cccl/libcudacxx/cuda_atomics_atomic_ref_small", atomicRefSmallLines},
};

std::map<std::string, int> countLines(const std::string &text) {
  std::map<std::string, int> counts;
  for (size_t begin = 0; begin < text.size();) {
    const auto end = text.find('\n', begin);
    const auto stop = end == std::string::npos ? text.size() : end;
    ++counts[text.substr(begin, stop - begin)];
    begin = stop + 1;
  }
  return counts;
}

void requireLines(std::string_view name, const std::string &printed) {
  const auto found = ExpectedLines.find(name);
  if (found == ExpectedLines.end()) return;
  const auto actual = countLines(printed);
  std::map<std::string, int> expected;
  for (const auto &line : found->second())
    ++expected[line];
  for (const auto &[line, count] : expected) {
    const auto seen = actual.find(line);
    const auto printedCount = seen == actual.end() ? 0 : seen->second;
    if (printedCount != count) FAIL("expected " << count << " x \"" << line << "\" but the output has " << printedCount);
  }
}

using Entry = int (*)(int, char **);

int invoke(const Entry &entry, std::string_view name, std::vector<std::string> arguments) {
  std::vector<char *> forwarded;
  for (auto &argument : arguments)
    forwarded.push_back(argument.data());
  forwarded.push_back(nullptr);
  int output[2];
  REQUIRE(::pipe(output) == 0);
  std::fflush(nullptr);
  const auto child = ::fork();
  if (child < 0) FAIL("fork failed");
  if (child == 0) {
    ::close(output[0]);
    ::dup2(output[1], STDOUT_FILENO);
    ::dup2(output[1], STDERR_FILENO);
    ::close(output[1]);
    ::on_exit(
        [](int code, void *) {
          std::fflush(nullptr);
          ::_exit(code);
        },
        nullptr);
    (void)conformance::deviceSelection();
    const auto directory = std::filesystem::path(executable).parent_path();
    if (!directory.empty() && ::chdir(directory.c_str()) != 0) std::exit(EXIT_FAILURE);
    std::exit(entry(static_cast<int>(arguments.size()), forwarded.data()));
  }
  ::close(output[1]);
  std::string printed;
  std::array<char, 4096> buffer{};
  for (ssize_t count; (count = ::read(output[0], buffer.data(), buffer.size())) != 0;) {
    if (count < 0) {
      if (errno == EINTR) continue;
      FAIL("reading the sample output failed");
    }
    std::fwrite(buffer.data(), 1, static_cast<size_t>(count), stdout);
    printed.append(buffer.data(), static_cast<size_t>(count));
  }
  ::close(output[0]);
  std::fflush(stdout);
  int status = 0;
  REQUIRE(::waitpid(child, &status, 0) == child);
  if (WIFSIGNALED(status)) FAIL("terminated by signal " << WTERMSIG(status));
  if (const auto marker = std::find_if(FailureMarkers.begin(), FailureMarkers.end(),
                                       [&](std::string_view m) { return printed.find(m) != std::string::npos; });
      WEXITSTATUS(status) == 0 && marker != FailureMarkers.end())
    FAIL("exited 0 but printed \"" << *marker << '"');
  if (WEXITSTATUS(status) == 0) requireLines(name, printed);
  return WEXITSTATUS(status);
}

class CaseInvoker final : public Catch::ITestInvoker {
public:
  CaseInvoker(Entry entry, std::string name, std::vector<std::string> arguments)
      : entry(entry), name(std::move(name)), arguments(std::move(arguments)) {}
  void invoke() const override {
    std::vector<std::string> forwarded{executable};
    forwarded.insert(forwarded.end(), arguments.begin(), arguments.end());
    const auto status = ::invoke(entry, name, std::move(forwarded));
    if (status == ExitWaived) SKIP("the test waived itself for the selected device");
    REQUIRE(status == 0);
  }

private:
  Entry entry;
  std::string name;
  std::vector<std::string> arguments;
};

void add(const char *identity, const char *tags, Entry entry, std::vector<std::string> arguments) {
  Catch::AutoReg(Catch::Detail::make_unique<CaseInvoker>(entry, identity, std::move(arguments)), CATCH_INTERNAL_LINEINFO,
                 Catch::StringRef(), Catch::NameAndTags{identity, tags});
}

} // namespace

#define CONFORMANCE_VENDOR_CASE(id, name, tags, ...) extern "C" int vendor_main_##id(int, char **);
CONFORMANCE_VENDOR_CASES
#undef CONFORMANCE_VENDOR_CASE

int main(int argc, char **argv) {
  executable = argv[0];
#define CONFORMANCE_VENDOR_CASE(id, name, tags, ...)                                                                                       \
  add(name, CONFORMANCE_VENDOR_TAGS tags, vendor_main_##id, std::vector<std::string>{__VA_ARGS__});
  CONFORMANCE_VENDOR_CASES
#undef CONFORMANCE_VENDOR_CASE
  return Catch::Session().run(argc, argv);
}
