#include "compiler.hpp"

#include <algorithm>
#include <cctype>
#include <cstdio>
#include <cstdlib>
#include <filesystem>
#include <fstream>
#include <iterator>
#include <limits>
#include <sstream>
#include <string>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <stddef.h>
#include <unistd.h>

#include <aspartame/string.hpp>
#include <aspartame/vector.hpp>

#include <sys/types.h>
#include <sys/wait.h>

namespace gemstone::opencl {

using namespace aspartame;

namespace {

class FileDescriptor {
public:
  explicit FileDescriptor(int value = -1) noexcept : value_(value) {}
  ~FileDescriptor() {
    if (value_ != -1) close(value_);
  }

  FileDescriptor(const FileDescriptor &) = delete;
  FileDescriptor &operator=(const FileDescriptor &) = delete;

  FileDescriptor(FileDescriptor &&other) noexcept : value_(std::exchange(other.value_, -1)) {}
  FileDescriptor &operator=(FileDescriptor &&other) noexcept {
    if (this != &other) {
      if (value_ != -1) close(value_);
      value_ = std::exchange(other.value_, -1);
    }
    return *this;
  }

  [[nodiscard]] int get() const noexcept { return value_; }
  [[nodiscard]] int release() noexcept { return std::exchange(value_, -1); }

private:
  int value_;
};

class TemporaryDirectory {
public:
  explicit TemporaryDirectory(std::string pattern) {
    std::vector<char> buffer(pattern.begin(), pattern.end());
    buffer.push_back('\0');
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

struct ProcessResult {
  int status = -1;
  std::string output;
};

ProcessResult runProcess(const std::vector<std::string> &arguments) {
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

bool splitOptions(const std::string &options, std::vector<std::string> &result, std::string *error) {
  std::string current;
  char quote = '\0';
  bool escaped = false;
  for (const char value : options) {
    if (escaped) {
      current.push_back(value);
      escaped = false;
    } else if (value == '\\' && quote != '\'') {
      escaped = true;
    } else if (quote != '\0') {
      if (value == quote) quote = '\0';
      else current.push_back(value);
    } else if (value == '\'' || value == '"') {
      quote = value;
    } else if (std::isspace(static_cast<unsigned char>(value))) {
      if (!current.empty()) {
        result.push_back(std::move(current));
        current.clear();
      }
    } else {
      current.push_back(value);
    }
  }
  if (escaped || quote != '\0') {
    if (error) *error = "unterminated escape or quote in OpenCL build options";
    return false;
  }
  if (!current.empty()) result.push_back(std::move(current));
  return true;
}

std::string compilerPath() {
  const char *configured = std::getenv("GEMSTONE_OPENCL_COMPILER");
  if (configured && configured[0] != '\0') return configured;
  return "/opt/intel/oneapi/compiler/2026.1/bin/icpx";
}

std::string spirvConverterPath() {
  const char *configured = std::getenv("GEMSTONE_OPENCL_LLVM_SPIRV");
  if (configured && configured[0] != '\0') return configured;
  return "/opt/intel/oneapi/compiler/2026.1/bin/compiler/llvm-spirv";
}

std::string bitcodeLinkerPath() {
  const char *configured = std::getenv("GEMSTONE_OPENCL_LLVM_LINK");
  if (configured && configured[0] != '\0') return configured;
  return "/opt/intel/oneapi/compiler/2026.1/bin/compiler/llvm-link";
}

std::string makeGlobalMemoryVolatile(const std::string &source) {
  static constexpr const char marker[] = "__global";
  std::string result;
  result.reserve(source.size() + source.size() / 32);
  size_t position = 0;
  while (position < source.size()) {
    const size_t found = source.find(marker, position);
    if (found == std::string::npos) {
      result.append(source, position, std::string::npos);
      break;
    }
    const bool startsWord = found == 0 || (!std::isalnum(static_cast<unsigned char>(source[found - 1])) && source[found - 1] != '_');
    const size_t afterMarker = found + sizeof(marker) - 1;
    const bool endsWord =
        afterMarker == source.size() || (!std::isalnum(static_cast<unsigned char>(source[afterMarker])) && source[afterMarker] != '_');
    result.append(source, position, found - position);
    result.append(marker);
    if (startsWord && endsWord) {
      size_t next = afterMarker;
      while (next < source.size() && std::isspace(static_cast<unsigned char>(source[next])))
        ++next;
      const bool alreadyVolatile = source.compare(next, sizeof("volatile") - 1, "volatile") == 0
                                   && (next + sizeof("volatile") - 1 == source.size()
                                       || (!std::isalnum(static_cast<unsigned char>(source[next + sizeof("volatile") - 1]))
                                           && source[next + sizeof("volatile") - 1] != '_'));
      if (!alreadyVolatile) result.append(" volatile");
    }
    position = afterMarker;
  }
  return result;
}

bool writeBytes(const std::string &path, const uint8_t *bytes, size_t size) {
  FileDescriptor descriptor(open(path.c_str(), O_WRONLY | O_CREAT | O_EXCL, 0600));
  if (descriptor.get() == -1) return false;
  size_t offset = 0;
  while (offset < size) {
    const ssize_t count = write(descriptor.get(), bytes + offset, size - offset);
    if (count <= 0) {
      unlink(path.c_str());
      return false;
    }
    offset += static_cast<size_t>(count);
  }
  return close(descriptor.release()) == 0;
}

bool writeFile(const std::string &path, const std::string &contents) {
  return writeBytes(path, reinterpret_cast<const uint8_t *>(contents.data()), contents.size());
}

bool readBytes(const std::string &path, std::vector<uint8_t> *bytes) {
  std::ifstream stream(path, std::ios::binary);
  if (!stream) return false;
  bytes->assign(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
  return stream.good() || stream.eof();
}

} // namespace

bool compileSource(const std::string &source, const std::string &options, std::vector<uint8_t> &il, std::string &log,
                   std::vector<uint8_t> *bitcodeOutput) {
  std::vector<std::string> optionArguments;
  std::string optionError;
  if (!splitOptions(options, optionArguments, &optionError)) {
    log = optionError;
    return false;
  }
  const TemporaryDirectory directory("/tmp/gemstone-opencl-XXXXXX");
  if (!directory) {
    log = "cannot create temporary directory for OpenCL compilation";
    return false;
  }
  const std::string input = directory.file("source.cl");
  const std::string bitcode = directory.file("source.bc");
  const std::string output = directory.file("source.spv");
  // The installed LLVM-to-SPIR-V converter rejects legal OpenCL swizzles when
  // the frontend narrows an otherwise unused vector lane (for example, a
  // char8 .even expression becomes an invalid LLVM <7 x i8>). Volatile global
  // accesses preserve the source semantics while forcing the frontend to keep
  // the declared vector width, which is representable in SPIR-V.
  const bool hasVectorSwizzle = (source ^ contains_slice(".hi")) || (source ^ contains_slice(".lo")) || (source ^ contains_slice(".even"))
                                || (source ^ contains_slice(".odd"));
  const bool usesEnqueuedLocalSize = source ^ contains_slice("get_enqueued_local_size");
  const std::string compilerSource = hasVectorSwizzle ? makeGlobalMemoryVolatile(source) : source;
  bool success = writeFile(input, compilerSource);
  std::string diagnostic;
  if (success) {
    std::vector<std::string> command = {compilerPath(), "-x",     "cl",         "-cl-std=CL1.2",  "-D__OPENCL_VERSION__=120",
                                        "-target",      "spir64", "-emit-llvm", "-fsigned-zeros", "-c",
                                        input,          "-o",     bitcode};
    static constexpr const char *unsupportedFeatures[] = {"__opencl_c_program_scope_global_variables",
                                                          "__opencl_c_3d_image_writes",
                                                          "__opencl_c_atomic_order_acq_rel",
                                                          "__opencl_c_atomic_order_seq_cst",
                                                          "__opencl_c_atomic_scope_device",
                                                          "__opencl_c_atomic_scope_all_devices",
                                                          "__opencl_c_device_enqueue",
                                                          "__opencl_c_generic_address_space",
                                                          "__opencl_c_pipes",
                                                          "__opencl_c_read_write_images",
                                                          "__opencl_c_work_group_collective_functions",
                                                          "__opencl_c_images",
                                                          "__opencl_c_integer_dot_product_input_4x8bit",
                                                          "__opencl_c_integer_dot_product_input_4x8bit_packed",
                                                          "__opencl_c_ext_image_unorm_int_2_101010",
                                                          "__IMAGE_SUPPORT__"};
    for (const auto *feature : unsupportedFeatures) {
      command.emplace_back("-U");
      command.emplace_back(feature);
    }
    for (const auto &argument : optionArguments)
      if (argument != "-cl-no-subgroup-ifp") command.push_back(argument);
    // Some OpenCL 3.0 CTS cases use 2.0 built-ins while the device advertises
    // the optional 1.2 C language profile. Compile those source strings as
    // CL3.0 so their conditional built-in code is present.
    if (usesEnqueuedLocalSize) {
      command.emplace_back("-cl-std=CL3.0");
      command.emplace_back("-U__OPENCL_VERSION__");
      command.emplace_back("-D__OPENCL_VERSION__=300");
    }
    const ProcessResult compilation = runProcess(command);
    diagnostic = compilation.output;
    success = compilation.status == 0;
  } else {
    diagnostic = "cannot write temporary OpenCL source";
  }
  if (success) {
    if (bitcodeOutput) {
      success = readBytes(bitcode, bitcodeOutput) && !bitcodeOutput->empty();
      if (!success) diagnostic += "cannot read generated LLVM bitcode";
    }
  }
  if (success) {
    const ProcessResult conversion = runProcess({spirvConverterPath(), bitcode, "-o", output});
    if (!conversion.output.empty()) diagnostic += conversion.output;
    success = conversion.status == 0;
  }
  if (success) {
    std::ifstream stream(output, std::ios::binary);
    if (!stream) {
      success = false;
      diagnostic += "cannot read generated SPIR-V";
    } else {
      il.assign(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
      success = !il.empty() && il.size() <= std::numeric_limits<uint32_t>::max();
      if (!success) diagnostic += "generated SPIR-V is empty or too large";
    }
  }
  log = diagnostic;
  return success;
}

bool reverseSpirv(const std::vector<uint8_t> &il, std::vector<uint8_t> &bitcode, std::string &log) {
  const TemporaryDirectory directory("/tmp/gemstone-opencl-reverse-XXXXXX");
  if (!directory) {
    log = "cannot create temporary directory for SPIR-V linking";
    return false;
  }
  const std::string input = directory.file("input.spv");
  const std::string output = directory.file("output.bc");
  bool success = writeBytes(input, il.data(), il.size());
  std::string diagnostic;
  if (success) {
    const ProcessResult conversion = runProcess({spirvConverterPath(), "-r", input, "-o", output});
    diagnostic = conversion.output;
    success = conversion.status == 0;
  }
  if (success) {
    success = readBytes(output, &bitcode) && !bitcode.empty();
    if (!success) diagnostic += "cannot read reverse-translated LLVM bitcode";
  }
  log = diagnostic;
  return success;
}

bool linkBitcode(const std::vector<std::vector<uint8_t>> &modules, std::vector<uint8_t> &il, std::vector<uint8_t> &bitcode,
                 std::string &log) {
  if (modules.empty()) return false;
  const TemporaryDirectory directory("/tmp/gemstone-opencl-link-XXXXXX");
  if (!directory) {
    log = "cannot create temporary directory for OpenCL linking";
    return false;
  }
  const std::string linkedBitcode = directory.file("linked.bc");
  const std::string output = directory.file("linked.spv");
  std::vector<std::string> inputs;
  inputs.reserve(modules.size());
  bool success = true;
  for (size_t index = 0; index < modules.size(); ++index) {
    const std::string path = directory.file("input" + std::to_string(index) + ".bc");
    if (!writeBytes(path, modules[index].data(), modules[index].size())) {
      success = false;
      break;
    }
    inputs.push_back(path);
  }
  std::string diagnostic;
  if (success) {
    std::vector<std::string> command = {bitcodeLinkerPath()};
    command.insert(command.end(), inputs.begin(), inputs.end());
    command.emplace_back("-o");
    command.push_back(linkedBitcode);
    const ProcessResult linking = runProcess(command);
    diagnostic = linking.output;
    success = linking.status == 0;
  }
  if (success) {
    success = readBytes(linkedBitcode, &bitcode) && !bitcode.empty();
    if (!success) diagnostic += "cannot read linked LLVM bitcode";
  }
  if (success) {
    const ProcessResult conversion = runProcess({spirvConverterPath(), linkedBitcode, "-o", output});
    if (!conversion.output.empty()) diagnostic += conversion.output;
    success = conversion.status == 0;
  }
  if (success) {
    success = readBytes(output, &il) && !il.empty() && il.size() <= std::numeric_limits<uint32_t>::max();
    if (!success) diagnostic += "generated linked SPIR-V is empty or too large";
  }
  log = diagnostic;
  return success;
}

std::string sourceKernelText(const std::string &source, const std::string &name) {
  auto identifier = [](char value) {
    const auto character = static_cast<unsigned char>(value);
    return std::isalnum(character) || value == '_';
  };
  size_t position = 0;
  while ((position = source.find(name, position)) != std::string::npos) {
    const bool leftBoundary = position == 0 || !identifier(source[position - 1]);
    size_t open = position + name.size();
    const bool rightBoundary = open == source.size() || !identifier(source[open]);
    while (open < source.size() && std::isspace(static_cast<unsigned char>(source[open])))
      ++open;
    if (!leftBoundary || !rightBoundary || open == source.size() || source[open] != '(') {
      position += name.size();
      continue;
    }
    const size_t priorSemicolon = source.rfind(';', position);
    const size_t priorBrace = source.rfind('}', position);
    const size_t prior = priorSemicolon == std::string::npos ? priorBrace
                         : priorBrace == std::string::npos   ? priorSemicolon
                                                             : std::max(priorSemicolon, priorBrace);
    const size_t start = prior == std::string::npos ? 0 : prior + 1;
    const size_t underscoredKernel = source.find("__kernel", start);
    const size_t plainKernel = source.find("kernel", start);
    const bool hasKernelQualifier = (underscoredKernel != std::string::npos && underscoredKernel < position)
                                    || (plainKernel != std::string::npos && plainKernel < position);
    if (!hasKernelQualifier) {
      position += name.size();
      continue;
    }
    size_t close = open;
    size_t depth = 0;
    for (; close < source.size(); ++close) {
      if (source[close] == '(') ++depth;
      else if (source[close] == ')' && --depth == 0) break;
    }
    if (close == source.size()) return {};
    const size_t body = source.find('{', close + 1);
    const size_t declarationEnd = source.find(';', close + 1);
    if (body == std::string::npos || (declarationEnd != std::string::npos && declarationEnd < body))
      return source.substr(start, close + 1 - start);
    size_t bodyEnd = body;
    depth = 0;
    for (; bodyEnd < source.size(); ++bodyEnd) {
      if (source[bodyEnd] == '{') ++depth;
      else if (source[bodyEnd] == '}' && --depth == 0) {
        ++bodyEnd;
        break;
      }
    }
    return source.substr(start, bodyEnd - start);
  }
  return {};
}

std::array<size_t, 3> sourceRequiredWorkGroupSize(const std::string &source) {
  std::array<size_t, 3> result{0, 0, 0};
  const std::string marker = "reqd_work_group_size(";
  const size_t start = source.find(marker);
  if (start == std::string::npos) return result;
  size_t position = start + marker.size();
  for (size_t index = 0; index < result.size(); ++index) {
    while (position < source.size() && std::isspace(static_cast<unsigned char>(source[position])))
      ++position;
    if (position == source.size() || !std::isdigit(static_cast<unsigned char>(source[position]))) return {0, 0, 0};
    size_t value = 0;
    while (position < source.size() && std::isdigit(static_cast<unsigned char>(source[position]))) {
      const size_t digit = static_cast<size_t>(source[position] - '0');
      if (value > (std::numeric_limits<size_t>::max() - digit) / 10) return {0, 0, 0};
      value = value * 10 + digit;
      ++position;
    }
    result[index] = value;
    while (position < source.size() && std::isspace(static_cast<unsigned char>(source[position])))
      ++position;
    if (index + 1 < result.size()) {
      if (position == source.size() || source[position] != ',') return {0, 0, 0};
      ++position;
    }
  }
  return result;
}

size_t sourceStaticLocalMemory(const std::string &source) {
  size_t total = 0;
  size_t position = 0;
  while ((position = source.find("__local", position)) != std::string::npos) {
    const size_t open = source.find('[', position + 7);
    const size_t close = open == std::string::npos ? std::string::npos : source.find(']', open + 1);
    if (open != std::string::npos && close != std::string::npos) {
      size_t count = 0;
      size_t digit = open + 1;
      while (digit < close && std::isspace(static_cast<unsigned char>(source[digit])))
        ++digit;
      while (digit < close && std::isdigit(static_cast<unsigned char>(source[digit]))) {
        count = count * 10 + static_cast<size_t>(source[digit] - '0');
        ++digit;
      }
      size_t typeStart = position + 7;
      while (typeStart < source.size() && std::isspace(static_cast<unsigned char>(source[typeStart])))
        ++typeStart;
      const size_t typeEnd = source.find_first_of(" \t\n\r", typeStart);
      const std::string type = source.substr(typeStart, typeEnd == std::string::npos ? open - typeStart : typeEnd - typeStart);
      const size_t elementSize = type.find("char") != std::string::npos    ? 1
                                 : type.find("short") != std::string::npos ? 2
                                 : type.find("long") != std::string::npos  ? 8
                                                                           : 4;
      if (count != 0 && count <= (std::numeric_limits<size_t>::max() - total) / elementSize) total += count * elementSize;
    }
    position += 7;
  }
  return total;
}

std::string sourceKernelAttributes(const std::string &source) {
  std::string result;
  const std::string marker = "__attribute__((";
  size_t position = 0;
  while ((position = source.find(marker, position)) != std::string::npos) {
    const size_t start = position + marker.size();
    const size_t end = source.find(")))", start);
    if (end == std::string::npos) break;
    if (!result.empty()) result.push_back(' ');
    result.append(source, start, end - start + 1);
    position = end + 3;
  }
  return result;
}

std::vector<KernelArgumentInfo> sourceKernelArgumentInfo(const std::string &source, const std::string &name) {
  std::vector<KernelArgumentInfo> result;
  const size_t namePosition = source.find(name);
  if (namePosition == std::string::npos) return result;
  const size_t open = source.find('(', namePosition + name.size());
  if (open == std::string::npos) return result;
  size_t close = open;
  size_t depth = 0;
  for (; close < source.size(); ++close) {
    if (source[close] == '(') ++depth;
    else if (source[close] == ')' && --depth == 0) break;
  }
  if (close >= source.size()) return result;
  const std::string parameters = source.substr(open + 1, close - open - 1);
  size_t start = 0;
  while (start < parameters.size()) {
    size_t end = start;
    size_t nested = 0;
    for (; end < parameters.size(); ++end) {
      if (parameters[end] == '(') ++nested;
      else if (parameters[end] == ')' && nested != 0) --nested;
      else if (parameters[end] == ',' && nested == 0) break;
    }
    std::string parameter = parameters.substr(start, end - start) ^ trim();
    if (!parameter.empty()) {
      const size_t nameEnd = parameter.find_last_not_of(" \t\n\r");
      size_t nameStart = nameEnd;
      while (nameStart != std::string::npos
             && (std::isalnum(static_cast<unsigned char>(parameter[nameStart])) || parameter[nameStart] == '_')) {
        if (nameStart == 0) break;
        --nameStart;
      }
      if (nameStart != 0 || !std::isalnum(static_cast<unsigned char>(parameter[nameStart]))) ++nameStart;
      KernelArgumentInfo info;
      if (nameStart <= nameEnd) {
        info.name = parameter.substr(nameStart, nameEnd - nameStart + 1);
        parameter.erase(nameStart);
      }
      const auto words = parameter ^ aspartame::words();
      std::vector<std::string> typeWords;
      bool pointer = false;
      bool unsignedType = false;
      bool signedType = false;
      for (auto word : words) {
        if (word == "global") info.address = CL_KERNEL_ARG_ADDRESS_GLOBAL;
        else if (word == "local") info.address = CL_KERNEL_ARG_ADDRESS_LOCAL;
        else if (word == "constant") info.address = CL_KERNEL_ARG_ADDRESS_CONSTANT;
        else if (word == "private") info.address = CL_KERNEL_ARG_ADDRESS_PRIVATE;
        else if (word == "read_only") info.access = CL_KERNEL_ARG_ACCESS_READ_ONLY;
        else if (word == "write_only") info.access = CL_KERNEL_ARG_ACCESS_WRITE_ONLY;
        else if (word == "read_write") info.access = CL_KERNEL_ARG_ACCESS_READ_WRITE;
        else if (word == "const") info.type |= CL_KERNEL_ARG_TYPE_CONST;
        else if (word == "volatile") info.type |= CL_KERNEL_ARG_TYPE_VOLATILE;
        else if (word == "restrict") info.type |= CL_KERNEL_ARG_TYPE_RESTRICT;
        else if (word == "pipe") info.type |= CL_KERNEL_ARG_TYPE_PIPE;
        else {
          if (word ^ contains('*')) pointer = true;
          word ^= filter([](char value) { return value != '*'; });
          if (word == "unsigned") unsignedType = true;
          else if (word == "signed") signedType = true;
          else if (!word.empty()) typeWords.push_back(word);
        }
      }
      std::string base = typeWords.empty() ? "int" : typeWords.back();
      if (base == "signed") base = "int";
      if (base == "unsigned") base = "int";
      if (unsignedType && base.rfind("u", 0) != 0) base = "u" + base;
      if (signedType && base == "") base = "int";
      info.typeName = base + (pointer ? "*" : "");
      if (info.address == CL_KERNEL_ARG_ADDRESS_CONSTANT) info.type |= CL_KERNEL_ARG_TYPE_CONST;
      if (!pointer) info.type = CL_KERNEL_ARG_TYPE_NONE;
      result.push_back(std::move(info));
    }
    if (end == parameters.size()) break;
    start = end + 1;
  }
  return result;
}

std::string withoutLibraryOption(const std::string &options) {
  std::vector<std::string> arguments;
  if (!splitOptions(options, arguments, nullptr)) return options;
  return arguments ^ filter([](const std::string &argument) { return argument != "-create-library"; })
         ^ map([](const std::string &argument) {
             const bool needsQuotes = argument ^ exists([](char value) { return std::isspace(static_cast<unsigned char>(value)) != 0; });
             return needsQuotes ? '"' + argument + '"' : argument;
           })
         ^ mk_string(" ");
}

namespace {

std::vector<std::string> includeDirectories(const std::string &options) {
  std::vector<std::string> arguments;
  if (!splitOptions(options, arguments, nullptr)) return {};
  std::vector<std::string> result;
  for (size_t index = 0; index < arguments.size(); ++index) {
    if (arguments[index] == "-I" && index + 1 < arguments.size()) result.push_back(arguments[++index]);
    else if (arguments[index].size() > 2 && (arguments[index] ^ starts_with("-I"))) result.push_back(arguments[index] ^ strip_prefix("-I"));
  }
  return result;
}

} // namespace

bool expandFilesystemIncludes(const std::string &source, const std::string &options, std::string &expanded, std::string &error,
                              unsigned depth) {
  if (depth > 16) {
    error = "OpenCL include nesting is too deep";
    return false;
  }
  const auto directories = includeDirectories(options);
  expanded = source;
  if (directories.empty()) return true;

  std::ostringstream result;
  size_t lineStart = 0;
  while (lineStart < source.size()) {
    const size_t lineEnd = source.find('\n', lineStart);
    const size_t length = lineEnd == std::string::npos ? source.size() - lineStart : lineEnd - lineStart;
    const std::string line = source.substr(lineStart, length);
    const size_t directive = line.find("#include");
    size_t nameStart = directive == std::string::npos ? std::string::npos : directive + 8;
    while (nameStart < line.size() && std::isspace(static_cast<unsigned char>(line[nameStart])))
      ++nameStart;
    const bool quoted = nameStart < line.size() && (line[nameStart] == '"' || line[nameStart] == '<');
    const char terminator = quoted ? (line[nameStart] == '"' ? '"' : '>') : '\0';
    const size_t nameEnd = quoted ? line.find(terminator, nameStart + 1) : std::string::npos;
    bool replaced = false;
    if (quoted && nameEnd != std::string::npos && nameEnd > nameStart + 1) {
      const std::string name = line.substr(nameStart + 1, nameEnd - nameStart - 1);
      for (const auto &directory : directories) {
        const std::string path = (directory.empty() || directory.back() == '/') ? directory + name : directory + "/" + name;
        std::ifstream header(path);
        if (!header) continue;
        const std::string contents{std::istreambuf_iterator<char>(header), std::istreambuf_iterator<char>()};
        std::string nested;
        if (!expandFilesystemIncludes(contents, options, nested, error, depth + 1)) return false;
        result << nested;
        replaced = true;
        break;
      }
    }
    if (!replaced) result << line;
    if (lineEnd != std::string::npos) result << '\n';
    if (lineEnd == std::string::npos) break;
    lineStart = lineEnd + 1;
  }
  expanded = result.str();
  return true;
}

} // namespace gemstone::opencl
