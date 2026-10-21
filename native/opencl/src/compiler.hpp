#pragma once

#include <array>
#include <string>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <CL/cl.h>

namespace gemstone::opencl {

struct KernelArgumentInfo {
  cl_kernel_arg_address_qualifier address = CL_KERNEL_ARG_ADDRESS_PRIVATE;
  cl_kernel_arg_access_qualifier access = CL_KERNEL_ARG_ACCESS_NONE;
  cl_kernel_arg_type_qualifier type = CL_KERNEL_ARG_TYPE_NONE;
  std::string typeName;
  std::string name;
};

// OpenCL C image types: image1d_t through image3d_t, including array, buffer, depth and multisample forms.
bool imageTypeName(const std::string &name);
std::string sourceKernelText(const std::string &source, const std::string &name);
size_t sourceStaticLocalMemory(const std::string &source);
std::string sourceKernelAttributes(const std::string &source);
std::vector<KernelArgumentInfo> sourceKernelArgumentInfo(const std::string &source, const std::string &name);
std::string withoutLibraryOption(const std::string &options);
bool expandFilesystemIncludes(const std::string &source, const std::string &options, std::string &expanded, std::string &error,
                              unsigned depth = 0);

bool deviceEnqueueSupported();

bool compileSourceToBitcode(const std::string &source, const std::string &options, std::vector<uint8_t> &bitcode, std::string &log);
bool compileSource(const std::string &source, const std::string &options, std::vector<uint8_t> &il, std::string &log,
                   std::vector<uint8_t> *bitcodeOutput = nullptr);
bool reverseSpirv(const std::vector<uint8_t> &il, std::vector<uint8_t> &bitcode, std::string &log);
bool linkBitcode(const std::vector<std::vector<uint8_t>> &modules, std::vector<uint8_t> &il, std::vector<uint8_t> &bitcode,
                 std::string &log);

} // namespace gemstone::opencl
