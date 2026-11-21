#include <charconv>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <fstream>
#include <ios>
#include <iterator>
#include <string>
#include <string_view>
#include <system_error>
#include <utility>
#include <vector>

namespace {

struct Link {
  std::vector<std::string> inputs;
  std::string log;
};

constexpr int Success = 0;
constexpr int PtxCompile = 4;
constexpr int Internal = 6;
constexpr int IncorrectInputType = 12;
constexpr int UnsupportedArch = 17;
constexpr int InputPtx = 2;

std::string cubinBytes() {
  const char *path = std::getenv("FAKE_NVJITLINK_CUBIN");
  std::ifstream file(path ? path : "", std::ios::binary);
  return {std::istreambuf_iterator<char>(file), std::istreambuf_iterator<char>()};
}

} // namespace

extern "C" {

int nvJitLinkCreate(Link **handle, uint32_t count, const char **options) {
  if (count != 1 || std::string(options[0]) != "-arch=sm_89") return UnsupportedArch;
  *handle = new Link;
  return Success;
}

int nvJitLinkDestroy(Link **handle) {
  delete *handle;
  *handle = nullptr;
  return Success;
}

int nvJitLinkAddData(Link *handle, int type, const void *data, size_t, const char *) {
  if (type != InputPtx) return IncorrectInputType;
  std::string text(static_cast<const char *>(data));
  if (text.contains(".version 99")) {
    handle->log = "ptxas fatal   : Unsupported .version 99.0; current version is '9.3'\n";
    return PtxCompile;
  }
  if (const auto at = text.find(".version"); at != std::string::npos) {
    const auto digits = text.find_first_not_of(" \t", at + std::string_view(".version").size());
    const char *const end = text.data() + text.size();
    int major = 0;
    int minor = 0;
    const auto [dot, majorError] = std::from_chars(text.data() + digits, end, major);
    const bool parsed = majorError == std::errc{} && dot != end && *dot == '.' && std::from_chars(dot + 1, end, minor).ec == std::errc{};
    if (parsed && (major < 3 || (major == 3 && minor < 1))) {
      handle->log = "ptxas fatal   : 'Compile only(-c)' requires PTX ISA .version 3.1 or later\n";
      return PtxCompile;
    }
  }
  if (!text.contains(".entry")) {
    handle->log = "ptxas fatal   : Parsing error\n";
    return PtxCompile;
  }
  handle->inputs.push_back(std::move(text));
  return Success;
}

int nvJitLinkComplete(Link *handle) {
  const char *expected = std::getenv("FAKE_NVJITLINK_INPUTS");
  return expected && handle->inputs.size() == std::stoul(expected) ? Success : Internal;
}

int nvJitLinkGetLinkedCubinSize(Link *, size_t *size) {
  *size = cubinBytes().size();
  return Success;
}

int nvJitLinkGetLinkedCubin(Link *, void *cubin) {
  const auto bytes = cubinBytes();
  bytes.copy(static_cast<char *>(cubin), bytes.size());
  return Success;
}

int nvJitLinkGetErrorLogSize(Link *handle, size_t *size) {
  *size = handle->log.empty() ? 0 : handle->log.size() + 1;
  return Success;
}

int nvJitLinkGetErrorLog(Link *handle, char *log) {
  handle->log.copy(log, handle->log.size());
  log[handle->log.size()] = '\0';
  return Success;
}
}
