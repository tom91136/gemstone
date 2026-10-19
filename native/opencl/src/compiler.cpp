#include "compiler.hpp"

#include <algorithm>
#include <cctype>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <filesystem>
#include <fstream>
#include <ios>
#include <iterator>
#include <limits>
#include <sstream>
#include <string>
#include <string_view>
#include <system_error>
#include <utility>
#include <vector>

#include <unistd.h>

#include <aspartame/string.hpp>
#include <aspartame/string_view.hpp>
#include <aspartame/vector.hpp>

#include <gemstone/environment.hpp>
#include <gemstone/process.hpp>

#include <environment_keys.hpp>

namespace gemstone::opencl {

using namespace aspartame;

namespace {

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

bool executable(const std::string &path) {
  if (path.empty() || std::filesystem::is_directory(path)) return false;
  return access(path.c_str(), X_OK) == 0;
}

std::string findOnPath(const std::string &name) {
  const char *path = std::getenv("PATH");
  if (!path || path[0] == '\0') return {};
  std::string_view remaining(path);
  while (!remaining.empty()) {
    const auto separator = remaining.find(':');
    const auto directory = remaining.substr(0, separator);
    remaining = separator == std::string_view::npos ? std::string_view{} : remaining.substr(separator + 1);
    if (directory.empty()) continue;
    const auto candidate = (std::filesystem::path(directory) / name).string();
    if (executable(candidate)) return candidate;
  }
  return {};
}

// Patterns hold at most one `*`, matching within a single path component.
std::vector<std::string> expandPattern(std::string_view pattern) {
  const auto star = pattern.find('*');
  if (star == std::string_view::npos) return {std::string(pattern)};
  const auto slash = pattern.rfind('/', star);
  const auto end = pattern.find('/', star);
  const auto prefix = pattern.substr(slash + 1, star - slash - 1);
  const auto suffix = pattern.substr(star + 1, end - star - 1);
  const auto rest = end == std::string_view::npos ? std::string_view{} : pattern.substr(end);
  std::vector<std::string> matches;
  std::error_code error;
  std::filesystem::directory_iterator entry(std::filesystem::path(pattern.substr(0, slash)), error);
  for (; !error && entry != std::filesystem::directory_iterator(); entry.increment(error)) {
    const auto name = entry->path().filename().string();
    if (name.size() >= prefix.size() + suffix.size() && name.starts_with(prefix) && name.ends_with(suffix))
      matches.push_back(entry->path().string().append(rest));
  }
  return matches;
}

// Newest match wins: toolchain directories are conventionally version ordered,
// so sorting the expansion keeps a rolling `latest` install ahead of an older one.
std::string findFirstGlob(const std::vector<std::string> &patterns) {
  for (const auto &pattern : patterns) {
    const auto newest = expandPattern(pattern) ^ filter([](const std::string &candidate) { return executable(candidate); }) ^ max();
    if (newest) return *newest;
  }
  return {};
}

std::string discoverExecutable(const std::vector<std::string> &names, const std::vector<std::string> &patterns) {
  for (const auto &name : names)
    if (const auto found = findOnPath(name); !found.empty()) return found;
  return findFirstGlob(patterns);
}

const std::string &compilerPath() {
  static const std::string path = [] {
    if (const char *configured = gemstone::environment::value(gemstone::environment::OpenclCompiler); configured && configured[0] != '\0')
      return std::string(configured);
    return discoverExecutable({"clang", "clang++"}, {"/opt/intel/oneapi/compiler/*/bin/compiler/clang", "/opt/rocm/llvm/bin/clang"});
  }();
  return path;
}

const std::string &spirvConverterPath() {
  static const std::string path = [] {
    if (const char *configured = gemstone::environment::value(gemstone::environment::OpenclLlvmSpirv); configured && configured[0] != '\0')
      return std::string(configured);
    return discoverExecutable({"llvm-spirv"},
                              {"/opt/intel/oneapi/compiler/*/bin/compiler/llvm-spirv", "/opt/rocm/llvm/bin/llvm-spirv",
                               "/usr/lib/llvm-*/bin/llvm-spirv", "/usr/lib64/llvm*/bin/llvm-spirv", "/usr/local/bin/llvm-spirv"});
  }();
  return path;
}

const std::string &bitcodeLinkerPath() {
  static const std::string path = [] {
    if (const char *configured = gemstone::environment::value(gemstone::environment::OpenclLlvmLink); configured && configured[0] != '\0')
      return std::string(configured);
    return discoverExecutable({"llvm-link"},
                              {"/opt/intel/oneapi/compiler/*/bin/compiler/llvm-link", "/opt/rocm/llvm/bin/llvm-link",
                               "/usr/lib/llvm-*/bin/llvm-link", "/usr/lib64/llvm*/bin/llvm-link", "/usr/local/bin/llvm-link"});
  }();
  return path;
}

// Only the SPIR-V extensions the interpreter executes.
constexpr const char *SpirvExtensions =
    "--spirv-ext=+SPV_KHR_subgroup_rotate,+SPV_KHR_bit_instructions,+SPV_INTEL_subgroups,+SPV_KHR_linkonce_odr,+SPV_KHR_shader_clock,"
    "+SPV_INTEL_bfloat16_conversion,+SPV_INTEL_split_barrier,+SPV_INTEL_media_block_io,"
    "+SPV_INTEL_subgroup_matrix_multiply_accumulate,+SPV_INTEL_2d_block_io,+SPV_INTEL_tensor_float32_conversion,"
    "+SPV_INTEL_ternary_bitwise_function,+SPV_INTEL_masked_gather_scatter,+SPV_INTEL_variable_length_array";

// Declarations of the Intel builtins the driver provides beyond clang's headers, which llvm-spirv translates by name
// or through the __spirv_ functions they call.
constexpr const char *IntelBuiltins = R"cl(
#define cl_intel_split_work_group_barrier 1
#define cl_intel_media_block_io 1
#define cl_intel_subgroup_matrix_multiply_accumulate 1
#define GEMSTONE_OVERLOAD __attribute__((overloadable))
void GEMSTONE_OVERLOAD intel_work_group_barrier_arrive(cl_mem_fence_flags flags);
void GEMSTONE_OVERLOAD intel_work_group_barrier_wait(cl_mem_fence_flags flags);
#if __OPENCL_C_VERSION__ == 200 || defined(__opencl_c_read_write_images)
#define GEMSTONE_MEDIA_READ_WRITE(T, S)                                                                                 \
  T GEMSTONE_OVERLOAD intel_sub_group_media_block_read_##S(int2 src_offset, int width, int height, read_write image2d_t image); \
  void GEMSTONE_OVERLOAD intel_sub_group_media_block_write_##S(int2 src_offset, int width, int height, T pixels,          \
                                                               read_write image2d_t image);
#else
#define GEMSTONE_MEDIA_READ_WRITE(T, S)
#endif
#define GEMSTONE_MEDIA(T, S)                                                                                            \
  T GEMSTONE_OVERLOAD intel_sub_group_media_block_read_##S(int2 src_offset, int width, int height, read_only image2d_t image); \
  void GEMSTONE_OVERLOAD intel_sub_group_media_block_write_##S(int2 src_offset, int width, int height, T pixels,          \
                                                               write_only image2d_t image);                              \
  GEMSTONE_MEDIA_READ_WRITE(T, S)
GEMSTONE_MEDIA(uchar, uc) GEMSTONE_MEDIA(uchar2, uc2) GEMSTONE_MEDIA(uchar4, uc4) GEMSTONE_MEDIA(uchar8, uc8)
GEMSTONE_MEDIA(uchar16, uc16) GEMSTONE_MEDIA(ushort, us) GEMSTONE_MEDIA(ushort2, us2) GEMSTONE_MEDIA(ushort4, us4)
GEMSTONE_MEDIA(ushort8, us8) GEMSTONE_MEDIA(ushort16, us16) GEMSTONE_MEDIA(uint, ui) GEMSTONE_MEDIA(uint2, ui2)
GEMSTONE_MEDIA(uint4, ui4) GEMSTONE_MEDIA(uint8, ui8)
#define GEMSTONE_MAD(NAME, R, A, K, OPERANDS)                                                                           \
  R GEMSTONE_OVERLOAD __spirv_SubgroupMatrixMultiplyAccumulateINTEL(int k, A a, int8 b, R c, int operands);           \
  R GEMSTONE_OVERLOAD NAME(A a, int8 b, R acc) {                                                                      \
    return __spirv_SubgroupMatrixMultiplyAccumulateINTEL(K, a, b, acc, OPERANDS);                                     \
  }
#define GEMSTONE_MADS(NAME, R1, R2, R4, R8, K, OPERANDS)                                                                \
  GEMSTONE_MAD(NAME, R1, int, K, OPERANDS) GEMSTONE_MAD(NAME, R2, int2, K, OPERANDS)                                  \
  GEMSTONE_MAD(NAME, R4, int4, K, OPERANDS) GEMSTONE_MAD(NAME, R8, int8, K, OPERANDS)                                 \
  GEMSTONE_MAD(NAME, R1, short, K, OPERANDS) GEMSTONE_MAD(NAME, R2, short2, K, OPERANDS)                              \
  GEMSTONE_MAD(NAME, R4, short4, K, OPERANDS) GEMSTONE_MAD(NAME, R8, short8, K, OPERANDS)
GEMSTONE_MADS(intel_sub_group_i8_i8_matrix_mad_k32, int, int2, int4, int8, 32, 0x33)
GEMSTONE_MADS(intel_sub_group_i8_u8_matrix_mad_k32, int, int2, int4, int8, 32, 0x31)
GEMSTONE_MADS(intel_sub_group_u8_i8_matrix_mad_k32, int, int2, int4, int8, 32, 0x32)
GEMSTONE_MADS(intel_sub_group_u8_u8_matrix_mad_k32, int, int2, int4, int8, 32, 0x30)
GEMSTONE_MADS(intel_sub_group_f16_f16_matrix_mad_k16, float, float2, float4, float8, 16, 0xc00)
GEMSTONE_MADS(intel_sub_group_bf16_bf16_matrix_mad_k16, float, float2, float4, float8, 16, 0x3000)
#define GEMSTONE_BFLOAT16(N)                                                                                            \
  ushort##N GEMSTONE_OVERLOAD intel_convert_bfloat16##N##_as_ushort##N(float##N source);                             \
  float##N GEMSTONE_OVERLOAD intel_convert_as_bfloat16##N##_float##N(ushort##N source);
GEMSTONE_BFLOAT16() GEMSTONE_BFLOAT16(2) GEMSTONE_BFLOAT16(3) GEMSTONE_BFLOAT16(4) GEMSTONE_BFLOAT16(8) GEMSTONE_BFLOAT16(16)
)cl";

// llvm-spirv is the historical translator, but current clang and icpx embed the
// same LLVM-to-SPIR-V backend and can consume bitcode directly. Falling back to
// the compiler keeps the runtime portable to hosts without a standalone
// llvm-spirv.
bool bitcodeToSpirv(const std::string &input, const std::string &output, std::string &diagnostic) {
  if (!spirvConverterPath().empty()) {
    const ProcessResult conversion = runProcess({spirvConverterPath(), SpirvExtensions, input, "-o", output});
    diagnostic += conversion.output;
    return conversion.status == 0;
  }
  if (compilerPath().empty()) {
    diagnostic += "no OpenCL compiler or llvm-spirv available for SPIR-V generation";
    return false;
  }
  const ProcessResult conversion = runProcess({compilerPath(), "-target", "spirv64", "-c", input, "-o", output});
  diagnostic += conversion.output;
  return conversion.status == 0;
}

// Calls visit(open, close, balanced) for each vload3/vstore3 call; visit returns false to stop scanning that builtin.
template <class Visit> void forEachVectorBuiltin(std::string_view source, Visit visit) {
  for (const std::string_view builtin : {"vload3", "vstore3"}) {
    size_t position = 0;
    while ((position = source.find(builtin, position)) != std::string_view::npos) {
      size_t open = position + builtin.size();
      while (open < source.size() && std::isspace(static_cast<unsigned char>(source[open])))
        ++open;
      if (open == source.size() || source[open] != '(') {
        position += builtin.size();
        continue;
      }
      int depth = 1;
      size_t close = open + 1;
      for (; close < source.size() && depth != 0; ++close) {
        if (source[close] == '(') ++depth;
        else if (source[close] == ')') --depth;
      }
      if (!visit(open, close, depth == 0)) break;
      position = close;
    }
  }
}

std::vector<std::string> vectorBuiltinPointers(std::string_view source) {
  std::vector<std::string> result;
  forEachVectorBuiltin(source, [&](size_t open, size_t close, bool balanced) {
    if (!balanced) return false;
    const size_t comma = source.rfind(',', close - 1);
    if (comma != std::string_view::npos && comma > open) {
      size_t begin = comma + 1;
      size_t end = close - 1;
      while (begin < end && std::isspace(static_cast<unsigned char>(source[begin])))
        ++begin;
      while (end > begin && std::isspace(static_cast<unsigned char>(source[end - 1])))
        --end;
      if (begin < end && (std::isalpha(static_cast<unsigned char>(source[begin])) || source[begin] == '_')
          && (source.substr(begin + 1, end - begin - 1)
              ^ forall([](char c) { return std::isalnum(static_cast<unsigned char>(c)) || c == '_'; })))
        result.emplace_back(source.substr(begin, end - begin));
    }
    return true;
  });
  return result;
}

// Whether a source position falls inside a vload3/vstore3 argument list, whose
// pointers must stay non-volatile.
bool insideVectorBuiltin(std::string_view source, size_t position) {
  bool inside = false;
  forEachVectorBuiltin(source, [&](size_t open, size_t close, bool) {
    inside = position > open && position < close;
    return !inside;
  });
  return inside;
}

std::string makeGlobalMemoryVolatile(const std::string &source) {
  static constexpr const char marker[] = "__global";
  const auto builtinPointers = vectorBuiltinPointers(source);
  std::string result;
  result.reserve(source.size() + (source.size() / 32));
  size_t position = 0;
  while (position < source.size()) {
    const size_t found = source.find(marker, position);
    if (found == std::string::npos) {
      result.append(source, position);
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
      bool usedByVectorBuiltin = false;
      bool castPointer = false;
      const size_t boundary = source.find_first_of(",;)", next);
      const size_t pointer = source.find('*', next);
      if (pointer != std::string::npos && (boundary == std::string::npos || pointer < boundary)) {
        size_t afterPointer = pointer + 1;
        while (afterPointer < source.size() && std::isspace(static_cast<unsigned char>(source[afterPointer])))
          ++afterPointer;
        // A cast such as (__global T*) declares no variable. Adding volatile
        // there makes vload/vstore overload resolution ambiguous, but casts
        // used for swizzled accesses still need it to keep the vector width.
        castPointer = afterPointer < source.size() && source[afterPointer] == ')' && insideVectorBuiltin(source, found);
        const size_t end = boundary == std::string::npos ? source.size() : boundary;
        std::string variable;
        for (size_t cursor = pointer + 1; cursor < end;) {
          if (!std::isalpha(static_cast<unsigned char>(source[cursor])) && source[cursor] != '_') {
            ++cursor;
            continue;
          }
          const size_t begin = cursor++;
          while (cursor < end && (std::isalnum(static_cast<unsigned char>(source[cursor])) || source[cursor] == '_'))
            ++cursor;
          variable = source.substr(begin, cursor - begin);
        }
        usedByVectorBuiltin = builtinPointers ^ contains(variable);
      }
      if (!alreadyVolatile && !usedByVectorBuiltin && !castPointer) result.append(" volatile");
    }
    position = afterMarker;
  }
  return result;
}

bool hasVectorComponentSwizzle(std::string_view source) {
  size_t position = 0;
  while ((position = source.find('.', position)) != std::string_view::npos) {
    size_t end = position + 1;
    if (end == source.size()) return false;
    const char first = source[end];
    if (first == 's' || first == 'S') {
      ++end;
      while (end < source.size() && std::isxdigit(static_cast<unsigned char>(source[end])))
        ++end;
      if (end == position + 2) {
        ++position;
        continue;
      }
    } else {
      const std::string_view components = "xyzwrgba";
      while (end < source.size() && components.contains(source[end]))
        ++end;
      if (end == position + 1) {
        ++position;
        continue;
      }
    }
    if (end == source.size() || (!std::isalnum(static_cast<unsigned char>(source[end])) && source[end] != '_')) return true;
    position = end;
  }
  return false;
}

bool readBytes(const std::string &path, std::vector<uint8_t> *bytes) {
  std::ifstream stream(path, std::ios::binary);
  if (!stream) return false;
  bytes->assign(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
  return stream.good() || stream.eof();
}

} // namespace

bool compileSourceToBitcode(const std::string &source, const std::string &options, std::vector<uint8_t> &bitcode, std::string &log) {
  if (compilerPath().empty()) {
    log = std::string("no OpenCL compiler found (set ") + gemstone::environment::OpenclCompiler + " or install clang)";
    return false;
  }
  std::vector<std::string> optionArguments;
  std::string optionError;
  if (!splitOptions(options, optionArguments, &optionError)) {
    log = optionError;
    return false;
  }
  const TemporaryDirectory directory("gemstone-opencl-XXXXXX");
  if (!directory) {
    log = "cannot create temporary directory for OpenCL compilation";
    return false;
  }
  const std::string input = directory.file("source.cl");
  const std::string output = directory.file("source.bc");
  // The installed LLVM-to-SPIR-V converter rejects legal OpenCL swizzles when
  // the frontend narrows an otherwise unused vector lane (for example, a
  // char8 .even expression becomes an invalid LLVM <7 x i8>, or a char2 .xx
  // expression becomes <1 x i8>). Volatile global
  // accesses preserve the source semantics while forcing the frontend to keep
  // the declared vector width, which is representable in SPIR-V.
  // vload3/vstore3 require non-volatile pointers, so leave those arguments'
  // declarations alone while preserving other vector accesses.
  const bool hasVectorSwizzle = (source ^ contains_slice(".hi")) || (source ^ contains_slice(".lo")) || (source ^ contains_slice(".even"))
                                || (source ^ contains_slice(".odd")) || hasVectorComponentSwizzle(source);
  const bool usesEnqueuedLocalSize = source ^ contains_slice("get_enqueued_local_size");
  const bool usesIntelSubgroups = source ^ contains_slice("intel_sub_group");
  const bool usesIntelBuiltins = (source ^ contains_slice("intel_work_group_barrier_"))
                                 || (source ^ contains_slice("intel_sub_group_media_block_")) || (source ^ contains_slice("_matrix_mad_k"))
                                 || (source ^ contains_slice("intel_convert_"));
  const std::string compilerSource = hasVectorSwizzle ? makeGlobalMemoryVolatile(source) : source;
  const std::string prelude = directory.file("intel.h");
  bool success = writeFile(input, compilerSource.data(), compilerSource.size())
                 && (!usesIntelBuiltins || writeFile(prelude, IntelBuiltins, std::strlen(IntelBuiltins)));
  std::string diagnostic;
  if (success) {
    // LLVM's SLP pass can pack four scalar boolean comparisons into i4,
    // which the installed LLVM-to-SPIR-V converter cannot represent.
    std::vector<std::string> command = {compilerPath(),
                                        "-x",
                                        "cl",
                                        "-cl-std=CL1.2",
                                        "-D__OPENCL_VERSION__=120",
                                        "-target",
                                        "spir64",
                                        "-emit-llvm",
                                        "-fsigned-zeros",
                                        "-fno-slp-vectorize",
                                        "-c",
                                        input,
                                        "-o",
                                        output};
    static constexpr const char *unsupportedFeatures[] = {"__opencl_c_ext_image_unorm_int_2_101010"};
    for (const auto *feature : unsupportedFeatures) {
      command.emplace_back("-U");
      command.emplace_back(feature);
    }
    command.insert(command.end(), {"-Xclang", "-cl-ext=+cl_intel_subgroups,+cl_khr_kernel_clock,+__opencl_c_kernel_clock_scope_device,"
                                              "+__opencl_c_kernel_clock_scope_work_group,+__opencl_c_kernel_clock_scope_sub_group,"
                                              "+cl_intel_bfloat16_conversions"});
    if (usesIntelBuiltins) command.insert(command.end(), {"-include", prelude});
    // The driver's built-in declaration tables omit the Intel subgroup functions; only the full header declares them, at
    // several times the compile cost.
    if (usesIntelSubgroups) command.insert(command.end(), {"-cl-no-stdinc", "-Xclang", "-finclude-default-header"});
    optionArguments ^ filter([](const std::string &argument) { return argument != "-cl-no-subgroup-ifp"; }) ^ append_to(command);
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
    success = readBytes(output, &bitcode) && !bitcode.empty();
    if (!success) diagnostic += "cannot read generated LLVM bitcode";
  }
  log = diagnostic;
  return success;
}

bool compileSource(const std::string &source, const std::string &options, std::vector<uint8_t> &il, std::string &log,
                   std::vector<uint8_t> *bitcodeOutput) {
  std::vector<uint8_t> bitcode;
  if (!compileSourceToBitcode(source, options, bitcode, log)) return false;
  const TemporaryDirectory directory("gemstone-opencl-spirv-XXXXXX");
  if (!directory) {
    log += "cannot create temporary directory for SPIR-V conversion";
    return false;
  }
  std::string diagnostic;
  const auto convert = [&](const std::vector<uint8_t> &candidate, const std::string &suffix) {
    const std::string input = directory.file("source" + suffix + ".bc");
    const std::string output = directory.file("source" + suffix + ".spv");
    if (!writeFile(input, candidate.data(), candidate.size())) {
      diagnostic = "cannot write generated LLVM bitcode";
      return false;
    }
    diagnostic.clear();
    if (!bitcodeToSpirv(input, output, diagnostic)) return false;
    if (!readBytes(output, &il)) {
      diagnostic += "cannot read generated SPIR-V";
      return false;
    }
    if (il.empty() || il.size() > std::numeric_limits<uint32_t>::max()) {
      diagnostic += "generated SPIR-V is empty or too large";
      return false;
    }
    return true;
  };
  bool success = convert(bitcode, "");
  if (!success && diagnostic.contains("InvalidBitWidth:")) {
    // Optimised LLVM IR can contain unusual integer widths (for example i33
    // from a closed-form loop sum) that the converter rejects. Preserve the
    // optimised path for ordinary programs and retry this converter failure.
    const std::string firstDiagnostic = diagnostic;
    std::vector<uint8_t> unoptimised;
    std::string fallbackLog;
    if (compileSourceToBitcode(source, options + " -O0", unoptimised, fallbackLog)) {
      success = convert(unoptimised, "-o0");
      if (success) {
        bitcode = std::move(unoptimised);
        diagnostic.clear();
      } else {
        diagnostic = firstDiagnostic + "\n-O0 fallback: " + diagnostic;
      }
    } else {
      diagnostic = firstDiagnostic + "\n-O0 fallback compilation: " + fallbackLog;
    }
  }
  if (!diagnostic.empty()) log += diagnostic;
  if (success && bitcodeOutput) *bitcodeOutput = std::move(bitcode);
  return success;
}

bool reverseSpirv(const std::vector<uint8_t> &il, std::vector<uint8_t> &bitcode, std::string &log) {
  const TemporaryDirectory directory("gemstone-opencl-reverse-XXXXXX");
  if (!directory) {
    log = "cannot create temporary directory for SPIR-V linking";
    return false;
  }
  const std::string input = directory.file("input.spv");
  const std::string output = directory.file("output.bc");
  if (spirvConverterPath().empty()) {
    log = "linking SPIR-V modules requires llvm-spirv for reverse translation";
    return false;
  }
  bool success = writeFile(input, il.data(), il.size());
  std::string diagnostic;
  if (success) {
    const ProcessResult conversion = runProcess({spirvConverterPath(), "-r", SpirvExtensions, input, "-o", output});
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
  if (bitcodeLinkerPath().empty()) {
    log = std::string("no llvm-link found (set ") + gemstone::environment::OpenclLlvmLink + " or install llvm-link)";
    return false;
  }
  const TemporaryDirectory directory("gemstone-opencl-link-XXXXXX");
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
    if (!writeFile(path, modules[index].data(), modules[index].size())) {
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
  // llvm-link drops a linkonce_odr definition that nothing linked so far references, even if a later module does;
  // relinking only what the composite still needs restores such definitions without duplicating strong symbols.
  const std::string relinkedBitcode = directory.file("relinked.bc");
  for (size_t pass = 0; success && pass < modules.size(); ++pass) {
    std::vector<std::string> command = {bitcodeLinkerPath(), "--only-needed", linkedBitcode};
    command.insert(command.end(), inputs.begin(), inputs.end());
    command.insert(command.end(), {"-o", relinkedBitcode});
    const ProcessResult relinking = runProcess(command);
    diagnostic += relinking.output;
    std::vector<uint8_t> relinked;
    success = relinking.status == 0 && readBytes(relinkedBitcode, &relinked) && !relinked.empty();
    if (!success || relinked == bitcode) break;
    bitcode = std::move(relinked);
    success = writeFile(linkedBitcode, bitcode.data(), bitcode.size());
  }
  if (success) success = bitcodeToSpirv(linkedBitcode, output, diagnostic);
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
    const bool bothFound = priorSemicolon != std::string::npos && priorBrace != std::string::npos;
    const size_t prior = bothFound ? std::max(priorSemicolon, priorBrace) : std::min(priorSemicolon, priorBrace);
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
      if (source[bodyEnd] == '{') {
        ++depth;
      } else if (source[bodyEnd] == '}' && --depth == 0) {
        ++bodyEnd;
        break;
      }
    }
    return source.substr(start, bodyEnd - start);
  }
  return {};
}

namespace {
// Byte size of an OpenCL C scalar or vector type name as written in a
// __local declaration. Vector widths (char2, float4, ...) multiply the scalar.
size_t openclElementSize(const std::string &type) {
  std::string base = type;
  size_t width = 0;
  size_t scale = 1;
  while (!base.empty() && std::isdigit(static_cast<unsigned char>(base.back()))) {
    width += static_cast<size_t>(base.back() - '0') * scale;
    scale *= 10;
    base.pop_back();
  }
  if (width == 0) width = 1;
  size_t baseSize = 4;
  if (base.contains("char")) baseSize = 1;
  else if (base.contains("short") || base == "half") baseSize = 2;
  else if (base.contains("long") || base.contains("double")) baseSize = 8;
  return baseSize * width;
}
} // namespace

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
        count = (count * 10) + static_cast<size_t>(source[digit] - '0');
        ++digit;
      }
      size_t typeStart = position + 7;
      while (typeStart < source.size() && std::isspace(static_cast<unsigned char>(source[typeStart])))
        ++typeStart;
      const size_t typeEnd = source.find_first_of(" \t\n\r", typeStart);
      const std::string type = source.substr(typeStart, typeEnd == std::string::npos ? open - typeStart : typeEnd - typeStart);
      const size_t elementSize = openclElementSize(type);
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
        if (word.starts_with("__")) word.erase(0, 2);
        if (word == "global") {
          info.address = CL_KERNEL_ARG_ADDRESS_GLOBAL;
        } else if (word == "local") {
          info.address = CL_KERNEL_ARG_ADDRESS_LOCAL;
        } else if (word == "constant") {
          info.address = CL_KERNEL_ARG_ADDRESS_CONSTANT;
        } else if (word == "private") {
          info.address = CL_KERNEL_ARG_ADDRESS_PRIVATE;
        } else if (word == "read_only") {
          info.access = CL_KERNEL_ARG_ACCESS_READ_ONLY;
        } else if (word == "write_only") {
          info.access = CL_KERNEL_ARG_ACCESS_WRITE_ONLY;
        } else if (word == "read_write") {
          info.access = CL_KERNEL_ARG_ACCESS_READ_WRITE;
        } else if (word == "const") {
          info.type |= CL_KERNEL_ARG_TYPE_CONST;
        } else if (word == "volatile") {
          info.type |= CL_KERNEL_ARG_TYPE_VOLATILE;
        } else if (word == "restrict") {
          info.type |= CL_KERNEL_ARG_TYPE_RESTRICT;
        } else if (word == "pipe") {
          info.type |= CL_KERNEL_ARG_TYPE_PIPE;
        } else {
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
      if (unsignedType && !base.starts_with('u')) base.insert(0, 1, 'u');
      if (signedType && base.empty()) base = "int";
      info.typeName = base + (pointer ? "*" : "");
      if (imageTypeName(info.typeName)) {
        info.address = CL_KERNEL_ARG_ADDRESS_GLOBAL;
        if (info.access == CL_KERNEL_ARG_ACCESS_NONE) info.access = CL_KERNEL_ARG_ACCESS_READ_ONLY;
      }
      if (info.address == CL_KERNEL_ARG_ADDRESS_CONSTANT) info.type |= CL_KERNEL_ARG_TYPE_CONST;
      if (!pointer) info.type &= CL_KERNEL_ARG_TYPE_PIPE;
      result.push_back(std::move(info));
    }
    if (end == parameters.size()) break;
    start = end + 1;
  }
  return result;
}

bool imageTypeName(const std::string &name) {
  return name.size() >= 9 && name.starts_with("image") && name[5] >= '1' && name[5] <= '3' && name[6] == 'd' && name.ends_with("_t");
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
    char terminator = '\0';
    if (quoted) terminator = line[nameStart] == '"' ? '"' : '>';
    const size_t nameEnd = quoted ? line.find(terminator, nameStart + 1) : std::string::npos;
    bool replaced = false;
    if (quoted && nameEnd != std::string::npos && nameEnd > nameStart + 1) {
      const std::string name = line.substr(nameStart + 1, nameEnd - nameStart - 1);
      for (const auto &directory : directories) {
        std::string path = directory;
        if (!path.empty() && path.back() != '/') path += '/';
        path += name;
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
