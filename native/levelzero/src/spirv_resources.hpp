#pragma once

#include <algorithm>
#include <cstdint>
#include <cstring>
#include <map>
#include <optional>
#include <set>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

namespace gemstone::levelzero {

struct KernelResources {
  uint32_t localBytes = 0;
  uint32_t privateBytes = 0;
  uint32_t spillBytes = 0;
};

// Register file of one emulated work-item, in 32-bit registers; live values beyond it spill to scratch.
inline constexpr uint32_t RegisterFileWords = 128;

namespace spirv_detail {

enum class Op : uint16_t {
  Nop = 0,
  Line = 8,
  EntryPoint = 15,
  TypeBool = 20,
  TypeInt = 21,
  TypeFloat = 22,
  TypeVector = 23,
  TypeArray = 28,
  TypeStruct = 30,
  TypePointer = 32,
  Constant = 43,
  Function = 54,
  FunctionCall = 57,
  FunctionEnd = 56,
  Variable = 59,
  Store = 62,
  CopyMemory = 63,
  CopyMemorySized = 64,
  ControlBarrier = 224,
  MemoryBarrier = 225,
  AtomicStore = 228,
  LoopMerge = 246,
  SelectionMerge = 247,
  Label = 248,
  Branch = 249,
  BranchConditional = 250,
  Switch = 251,
  Return = 253,
  ReturnValue = 254,
  Unreachable = 255,
  LifetimeStart = 256,
  LifetimeStop = 257,
  NoLine = 317
};

enum class StorageClass : uint32_t { Workgroup = 4, Function = 7 };

inline bool producesResult(Op op) {
  switch (op) {
    case Op::Nop:
    case Op::Line:
    case Op::NoLine:
    case Op::FunctionEnd:
    case Op::Store:
    case Op::CopyMemory:
    case Op::CopyMemorySized:
    case Op::ControlBarrier:
    case Op::MemoryBarrier:
    case Op::AtomicStore:
    case Op::LoopMerge:
    case Op::SelectionMerge:
    case Op::Label:
    case Op::Branch:
    case Op::BranchConditional:
    case Op::Switch:
    case Op::Return:
    case Op::ReturnValue:
    case Op::Unreachable:
    case Op::LifetimeStart:
    case Op::LifetimeStop: return false;
    default: return true;
  }
}

struct Instruction {
  Op op;
  const uint32_t *words;
  uint16_t count;
};

struct Types {
  std::map<uint32_t, Instruction> declarations;
  std::map<uint32_t, uint64_t> constants;

  [[nodiscard]] uint64_t bytes(uint32_t id) const {
    const auto found = declarations.find(id);
    if (found == declarations.end()) return 4;
    const auto &type = found->second;
    switch (type.op) {
      case Op::TypeBool: return 1;
      case Op::TypeInt:
      case Op::TypeFloat: return std::max<uint64_t>(type.words[2] / 8, 1);
      case Op::TypeVector: return bytes(type.words[2]) * type.words[3];
      case Op::TypeArray: {
        const auto length = constants.find(type.words[3]);
        return bytes(type.words[2]) * (length == constants.end() ? 1 : length->second);
      }
      case Op::TypeStruct: {
        uint64_t total = 0;
        for (uint16_t member = 2; member < type.count; ++member)
          total += bytes(type.words[member]);
        return total;
      }
      case Op::TypePointer: return 8;
      default: return 4;
    }
  }

  [[nodiscard]] uint32_t pointeeBytes(uint32_t pointer) const {
    const auto found = declarations.find(pointer);
    if (found == declarations.end() || found->second.op != Op::TypePointer) return 0;
    return static_cast<uint32_t>(bytes(found->second.words[3]));
  }

  [[nodiscard]] uint32_t registerWords(uint32_t id) const { return static_cast<uint32_t>((bytes(id) + 3) / 4); }
};

inline std::vector<Instruction> decode(const std::vector<uint8_t> &image, std::vector<uint32_t> &storage) {
  std::vector<Instruction> result;
  if (image.size() < 20 || image.size() % 4 != 0) return result;
  storage.resize(image.size() / 4);
  std::memcpy(storage.data(), image.data(), image.size());
  if (storage[0] != 0x07230203U) return {};
  for (size_t index = 5; index < storage.size();) {
    const auto count = static_cast<uint16_t>(storage[index] >> 16);
    if (count == 0 || index + count > storage.size()) return {};
    result.push_back({.op = static_cast<Op>(storage[index] & 0xffffU), .words = &storage[index], .count = count});
    index += count;
  }
  return result;
}

inline std::string literalString(const uint32_t *words, size_t count) {
  std::string text;
  for (size_t index = 0; index < count; ++index) {
    for (int byte = 0; byte < 4; ++byte) {
      const char ch = static_cast<char>((words[index] >> (8 * byte)) & 0xffU);
      if (ch == '\0') return text;
      text.push_back(ch);
    }
  }
  return text;
}

} // namespace spirv_detail

inline std::optional<KernelResources> kernelResources(const std::vector<uint8_t> &image, std::string_view kernel) {
  using namespace spirv_detail;
  std::vector<uint32_t> storage;
  const auto instructions = decode(image, storage);
  if (instructions.empty()) return std::nullopt;
  Types types;
  std::optional<uint32_t> entry;
  for (const auto &instruction : instructions) {
    switch (instruction.op) {
      case Op::TypeBool:
      case Op::TypeInt:
      case Op::TypeFloat:
      case Op::TypeVector:
      case Op::TypeArray:
      case Op::TypeStruct:
      case Op::TypePointer: types.declarations[instruction.words[1]] = instruction; break;
      case Op::Constant:
        if (instruction.count >= 4) types.constants[instruction.words[2]] = instruction.words[3];
        break;
      case Op::EntryPoint:
        if (instruction.count > 3 && literalString(instruction.words + 3, instruction.count - 3) == kernel) entry = instruction.words[2];
        break;
      default: break;
    }
  }
  if (!entry) return std::nullopt;

  std::map<uint32_t, uint32_t> workgroupVariables;
  for (const auto &instruction : instructions) {
    if (instruction.op != Op::Variable || instruction.count < 4
        || static_cast<StorageClass>(instruction.words[3]) != StorageClass::Workgroup)
      continue;
    workgroupVariables[instruction.words[2]] = types.pointeeBytes(instruction.words[1]);
  }

  std::map<uint32_t, std::vector<Instruction>> bodies;
  uint32_t current = 0;
  for (const auto &instruction : instructions) {
    if (instruction.op == Op::Function) current = instruction.words[2];
    else if (instruction.op == Op::FunctionEnd) current = 0;
    else if (current != 0) bodies[current].push_back(instruction);
  }

  struct Summary {
    int64_t peak = 0;
    uint32_t privateBytes = 0;
    std::set<uint32_t> workgroup;
  };
  std::map<uint32_t, Summary> summaries;
  std::set<uint32_t> active;
  const auto summarise = [&](auto &&self, uint32_t function) -> Summary {
    if (const auto done = summaries.find(function); done != summaries.end()) return done->second;
    Summary summary;
    const auto body = bodies.find(function);
    if (body == bodies.end() || !active.insert(function).second) return summary;
    struct Value {
      size_t defined;
      size_t lastUse;
      uint32_t words;
    };
    std::map<uint32_t, Value> values;
    std::map<size_t, uint32_t> callees;
    size_t position = 0;
    for (const auto &instruction : body->second) {
      ++position;
      const bool result = producesResult(instruction.op) && instruction.count >= 3;
      for (uint16_t operand = result ? 3 : 1; operand < instruction.count; ++operand) {
        const auto word = instruction.words[operand];
        if (const auto used = values.find(word); used != values.end()) used->second.lastUse = position;
        if (workgroupVariables.contains(word)) summary.workgroup.insert(word);
      }
      if (instruction.op == Op::FunctionCall && instruction.count >= 4) callees.emplace(position, instruction.words[3]);
      if (!result) continue;
      if (instruction.op == Op::Variable && instruction.count >= 4
          && static_cast<StorageClass>(instruction.words[3]) == StorageClass::Function) {
        summary.privateBytes += types.pointeeBytes(instruction.words[1]);
        continue;
      }
      values[instruction.words[2]] = {.defined = position, .lastUse = position, .words = types.registerWords(instruction.words[1])};
    }
    std::map<size_t, int64_t> deltas;
    for (const auto &[_, value] : values) {
      deltas[value.defined] += value.words;
      deltas[value.lastUse + 1] -= value.words;
    }
    for (const auto &[at, _] : callees)
      deltas.try_emplace(at, 0);
    int64_t live = 0;
    for (const auto &[at, delta] : deltas) {
      live += delta;
      summary.peak = std::max(summary.peak, live);
      if (const auto call = callees.find(at); call != callees.end()) {
        const auto nested = self(self, call->second);
        summary.peak = std::max(summary.peak, live + nested.peak);
        summary.privateBytes += nested.privateBytes;
        summary.workgroup.insert(nested.workgroup.begin(), nested.workgroup.end());
      }
    }
    active.erase(function);
    summaries[function] = summary;
    return summary;
  };

  const auto kernelSummary = summarise(summarise, *entry);
  KernelResources resources;
  resources.privateBytes = kernelSummary.privateBytes;
  for (const auto id : kernelSummary.workgroup)
    resources.localBytes += workgroupVariables.at(id);
  if (std::cmp_greater(kernelSummary.peak, RegisterFileWords))
    resources.spillBytes = static_cast<uint32_t>((kernelSummary.peak - RegisterFileWords) * 4);
  return resources;
}

} // namespace gemstone::levelzero
