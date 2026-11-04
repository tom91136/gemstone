#include "../src/spirv_resources.hpp"

#include <cstdint>
#include <cstring>
#include <initializer_list>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>

using gemstone::levelzero::KernelResources;
using gemstone::levelzero::kernelResources;
using gemstone::levelzero::RegisterFileWords;

namespace {
struct Module {
  std::vector<uint32_t> words{0x07230203U, 0x00010400U, 0U, 0U, 0U};

  void emit(uint16_t op, std::initializer_list<uint32_t> operands) {
    words.push_back((static_cast<uint32_t>(operands.size() + 1) << 16) | op);
    words.insert(words.end(), operands.begin(), operands.end());
  }

  void entry(uint32_t function, const std::string &name) {
    std::vector<uint32_t> literal((name.size() + 4) / 4, 0);
    std::memcpy(literal.data(), name.data(), name.size());
    words.push_back((static_cast<uint32_t>(literal.size() + 3) << 16) | 15U);
    words.push_back(6U);
    words.push_back(function);
    words.insert(words.end(), literal.begin(), literal.end());
  }

  [[nodiscard]] std::vector<uint8_t> image() {
    words[3] = 4096;
    std::vector<uint8_t> bytes(words.size() * 4);
    std::memcpy(bytes.data(), words.data(), bytes.size());
    return bytes;
  }
};

constexpr uint32_t Void = 1, Fn = 2, I32 = 3, Four = 4, ArrayType = 5, PrivatePointer = 6, LocalPointer = 7, LocalArray = 8, Kernel = 9;

Module scaffold() {
  Module module;
  module.entry(Kernel, "k");
  module.emit(19, {Void});
  module.emit(33, {Fn, Void});
  module.emit(21, {I32, 32, 0});
  module.emit(43, {I32, Four, 4});
  module.emit(28, {ArrayType, I32, Four});
  module.emit(32, {PrivatePointer, 7, ArrayType});
  module.emit(28, {LocalArray, I32, Four});
  module.emit(32, {LocalPointer, 4, LocalArray});
  module.emit(59, {LocalPointer, 20, 4});
  return module;
}

void emitRegisterPressure(Module &module) {
  constexpr uint32_t live = RegisterFileWords + 10;
  for (uint32_t index = 0; index < live; ++index)
    module.emit(128, {I32, 100 + index, Four, Four});
  uint32_t sum = 100;
  for (uint32_t index = 1; index < live; ++index) {
    module.emit(128, {I32, 1000 + index, sum, 100 + index});
    sum = 1000 + index;
  }
}
} // namespace

TEST_CASE("Level Zero kernel resources count function and workgroup storage", "[level-zero][spirv]") {
  auto module = scaffold();
  module.emit(54, {Void, Kernel, 0, Fn});
  module.emit(248, {30});
  module.emit(59, {PrivatePointer, 31, 7});
  module.emit(62, {20, 4});
  module.emit(253, {});
  module.emit(56, {});
  const auto resources = kernelResources(module.image(), "k").value_or(KernelResources{});
  CHECK(resources.privateBytes == 16);
  CHECK(resources.localBytes == 16);
  CHECK(resources.spillBytes == 0);
}

TEST_CASE("Level Zero kernel resources spill values live beyond the register file", "[level-zero][spirv]") {
  auto module = scaffold();
  module.emit(54, {Void, Kernel, 0, Fn});
  module.emit(248, {30});
  emitRegisterPressure(module);
  module.emit(253, {});
  module.emit(56, {});
  const auto resources = kernelResources(module.image(), "k").value_or(KernelResources{});
  CHECK(resources.spillBytes > 0);

  auto small = scaffold();
  small.emit(54, {Void, Kernel, 0, Fn});
  small.emit(248, {30});
  small.emit(128, {I32, 100, Four, Four});
  small.emit(253, {});
  small.emit(56, {});
  CHECK(kernelResources(small.image(), "k").value_or(KernelResources{}).spillBytes == 0);
}

TEST_CASE("Level Zero kernel resources require a named entry point", "[level-zero][spirv]") {
  auto module = scaffold();
  module.emit(54, {Void, Kernel, 0, Fn});
  module.emit(56, {});
  CHECK_FALSE(kernelResources(module.image(), "missing").has_value());
  CHECK_FALSE(kernelResources(std::vector<uint8_t>(8, 0), "k").has_value());
}

TEST_CASE("Level Zero kernel resources include the callees of the entry point", "[level-zero][spirv]") {
  constexpr uint32_t helper = 50;
  auto module = scaffold();
  module.emit(54, {Void, helper, 0, Fn});
  module.emit(248, {60});
  emitRegisterPressure(module);
  module.emit(253, {});
  module.emit(56, {});
  module.emit(54, {Void, Kernel, 0, Fn});
  module.emit(248, {30});
  module.emit(57, {Void, 31, helper});
  module.emit(253, {});
  module.emit(56, {});
  const auto resources = kernelResources(module.image(), "k").value_or(KernelResources{});
  CHECK(resources.spillBytes > 0);
}
