#include <filesystem>
#include <fstream>
#include <optional>
#include <string>
#include <system_error>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include <gemstone/test_support.hpp>

#include "session.hpp"

namespace {
struct CountedCompiler {
  std::filesystem::path directory;
  std::optional<gemstone_test::Environment> compiler;
  CountedCompiler() {
    auto pattern = (std::filesystem::temp_directory_path() / "gemstone-build-cache-XXXXXX").string();
    REQUIRE((mkdtemp(pattern.data()) != nullptr));
    directory = pattern;
    std::ofstream(directory / "cc") << "#!/bin/sh\necho x >> '" << (directory / "calls").string() << "'\nexec clang \"$@\"\n";
    std::filesystem::permissions(directory / "cc", std::filesystem::perms::owner_all);
    compiler.emplace("GEMSTONE_OPENCL_COMPILER_EXE", (directory / "cc").c_str());
  }
  CountedCompiler(const CountedCompiler &) = delete;
  CountedCompiler &operator=(const CountedCompiler &) = delete;
  ~CountedCompiler() {
    compiler.reset();
    std::error_code error;
    std::filesystem::remove_all(directory, error);
  }
  [[nodiscard]] int calls() const {
    std::ifstream stream(directory / "calls");
    int count = 0;
    for (std::string line; std::getline(stream, line);)
      ++count;
    return count;
  }
};

cl_int build(const gemstone_opencl_test::Session &session, const char *source, const char *options) {
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  const auto status = clBuildProgram(program, 1, &session.device, options, nullptr, nullptr);
  clReleaseProgram(program);
  return status;
}
} // namespace

TEST_CASE("OpenCL reuses compiled programs unless the build reads files", "[opencl][build]") {
  CountedCompiler const compiler;
  gemstone_opencl_test::Session const session;
  const char *source = "__kernel void k(__global int *out) { out[0] = 1; }";

  REQUIRE((build(session, source, "-cl-std=CL3.0") == CL_SUCCESS));
  const int first = compiler.calls();
  REQUIRE((first > 0));

  REQUIRE((build(session, source, "-cl-std=CL3.0") == CL_SUCCESS));
  CHECK((compiler.calls() == first));

  REQUIRE((build(session, source, "-cl-std=CL3.0 -DVALUE=1") == CL_SUCCESS));
  CHECK((compiler.calls() > first));

  const auto header = compiler.directory / "value.h";
  const std::string option = "-I" + compiler.directory.string();
  const char *included = "#include \"value.h\"\n__kernel void k(__global int *out) { out[0] = VALUE; }";

  std::ofstream(header) << "#define VALUE 1\n";
  REQUIRE((build(session, included, option.c_str()) == CL_SUCCESS));
  const int withHeader = compiler.calls();

  std::ofstream(header) << "#define VALUE 2\n";
  REQUIRE((build(session, included, option.c_str()) == CL_SUCCESS));
  CHECK((compiler.calls() > withHeader));
}
