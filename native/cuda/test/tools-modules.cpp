#include <array>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <elf.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");
const gemstone_test::Environment Target(gemstone::environment::TargetArch, "sm_89");

constexpr char Image[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.visible .entry first(.param .u64 p)
{
  ret;
}

.visible .entry second(.param .u64 p)
{
  ret;
}
)PTX";

using ExportId = std::array<unsigned char, 16>;

constexpr ExportId ModulesId{0x6e, 0x16, 0x3f, 0xbe, 0xb9, 0x58, 0x44, 0x4d, 0x83, 0x5c, 0xe1, 0x82, 0xaf, 0xf1, 0x99, 0x1e};
constexpr ExportId ContextId{0x21, 0x31, 0x8c, 0x60, 0x97, 0x14, 0x32, 0x48, 0x8c, 0xa6, 0x41, 0xff, 0x73, 0x24, 0xc8, 0xf2};

constexpr size_t ModuleLoadSlot = 28;
constexpr size_t ModuleFunctionSlot = 6;
constexpr size_t FunctionAddressSlot = 39;
constexpr size_t ModuleOriginSlot = 65;
constexpr size_t ModuleSerialSlot = 11;
constexpr size_t ModuleFlavourSlot = 27;
constexpr size_t ContextModulesSlot = 35;
constexpr uint32_t LoadedByToolsOrigin = 5;

using ModuleLoad = CUresult (*)(CUcontext, CUmodule *, const void *, uint64_t, uint64_t, uint64_t);
using ModuleFunction = CUresult (*)(CUcontext, CUmodule, const char *, CUfunction *);
using FunctionAddress = CUresult (*)(CUfunction, uint64_t *);
using ModuleWord = CUresult (*)(CUmodule, uint32_t *);

struct ModuleInfo {
  uint32_t size;
  uint32_t version;
  CUmodule module;
  const void *image;
  uint64_t imageSize;
  uint64_t reserved;
};

struct ModuleVisit {
  uint64_t reserved;
  CUcontext context;
  void (*callback)(void *, const ModuleInfo *);
  void *argument;
};

using ContextModules = CUresult (*)(ModuleVisit *);

template <class Function> Function slot(const ExportId &id, size_t index) {
  const void *table = nullptr;
  REQUIRE(cuGetExportTable(&table, reinterpret_cast<const CUuuid *>(id.data())) == CUDA_SUCCESS);
  return reinterpret_cast<Function>(reinterpret_cast<uintptr_t>(static_cast<const void *const *>(table)[index]));
}

CUcontext createContext() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  return context;
}

CUmodule loadThroughTools(CUcontext context) {
  CUmodule module = nullptr;
  REQUIRE(slot<ModuleLoad>(ModulesId, ModuleLoadSlot)(context, &module, Image, 0, 0, 0) == CUDA_SUCCESS);
  REQUIRE(module != nullptr);
  return module;
}

CUmodule loadThroughDriver() {
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, Image) == CUDA_SUCCESS);
  return module;
}

uint32_t moduleWord(size_t index, CUmodule module) {
  uint32_t value = 0xa5a5a5a5;
  REQUIRE(slot<ModuleWord>(ModulesId, index)(module, &value) == CUDA_SUCCESS);
  return value;
}

std::vector<unsigned char> cubinOf(const std::string &function) {
  const std::string strings = std::string(1, '\0') + function + std::string(1, '\0');
  const Elf64_Sym symbols[2]{
      {}, {.st_name = 1, .st_info = ELF64_ST_INFO(STB_LOCAL, STT_FUNC), .st_other = 0, .st_shndx = 0, .st_value = 0, .st_size = 0}};
  const auto stringsOffset = sizeof(Elf64_Ehdr);
  const auto symbolsOffset = (stringsOffset + strings.size() + 7) & ~size_t{7};
  const auto sectionsOffset = symbolsOffset + sizeof(symbols);
  Elf64_Shdr sections[3]{};
  Elf64_Shdr &symbolTable = sections[1];
  symbolTable.sh_type = SHT_SYMTAB;
  symbolTable.sh_offset = symbolsOffset;
  symbolTable.sh_size = sizeof(symbols);
  symbolTable.sh_link = 2;
  symbolTable.sh_entsize = sizeof(Elf64_Sym);
  Elf64_Shdr &stringTable = sections[2];
  stringTable.sh_type = SHT_STRTAB;
  stringTable.sh_offset = stringsOffset;
  stringTable.sh_size = strings.size();
  Elf64_Ehdr header{};
  std::memcpy(header.e_ident, ELFMAG, SELFMAG);
  header.e_ident[EI_CLASS] = ELFCLASS64;
  header.e_ident[EI_DATA] = ELFDATA2LSB;
  header.e_ident[EI_VERSION] = EV_CURRENT;
  header.e_type = ET_REL;
  header.e_machine = EM_CUDA;
  header.e_version = EV_CURRENT;
  header.e_shoff = sectionsOffset;
  header.e_ehsize = sizeof(Elf64_Ehdr);
  header.e_shentsize = sizeof(Elf64_Shdr);
  header.e_shnum = 3;
  std::vector<unsigned char> image(sectionsOffset + sizeof(sections));
  std::memcpy(image.data(), &header, sizeof(header));
  std::memcpy(image.data() + stringsOffset, strings.data(), strings.size());
  std::memcpy(image.data() + symbolsOffset, symbols, sizeof(symbols));
  std::memcpy(image.data() + sectionsOffset, sections, sizeof(sections));
  return image;
}

} // namespace

TEST_CASE("CUDA tools module table loads a module and resolves its functions", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const auto load = slot<ModuleLoad>(ModulesId, ModuleLoadSlot);
  CUmodule module = nullptr;
  CHECK(load(context, &module, nullptr, 0, 0, 0) == CUDA_ERROR_INVALID_IMAGE);
  CHECK(module == nullptr);
  module = loadThroughTools(context);

  const auto lookup = slot<ModuleFunction>(ModulesId, ModuleFunctionSlot);
  CUfunction first = nullptr;
  CUfunction driverFirst = nullptr;
  CHECK(lookup(context, module, "first", &first) == CUDA_SUCCESS);
  CHECK(cuModuleGetFunction(&driverFirst, module, "first") == CUDA_SUCCESS);
  CHECK(first == driverFirst);
  CUfunction second = nullptr;
  CHECK(lookup(context, module, "second", &second) == CUDA_SUCCESS);
  CHECK(second != first);
  CHECK(lookup(context, nullptr, "first", &first) == CUDA_ERROR_INVALID_VALUE);
  CHECK(lookup(context, module, nullptr, &first) == CUDA_ERROR_INVALID_VALUE);
  CHECK(lookup(context, module, "first", nullptr) == CUDA_ERROR_INVALID_VALUE);

  const auto address = slot<FunctionAddress>(ModulesId, FunctionAddressSlot);
  uint64_t firstAddress = 0;
  uint64_t secondAddress = 0;
  CHECK(address(first, &firstAddress) == CUDA_SUCCESS);
  CHECK(address(second, &secondAddress) == CUDA_SUCCESS);
  CHECK(firstAddress != 0);
  CHECK(firstAddress != secondAddress);
  CHECK(address(first, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(address(nullptr, &firstAddress) == CUDA_ERROR_INVALID_HANDLE);

  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA tools module table reports the origin and serial of a module", "[cuda][tools-modules]") {
  auto *const context = createContext();
  auto *const unknownModule = reinterpret_cast<CUmodule>(0x10);
  auto *const driverModule = loadThroughDriver();
  auto *const toolsModule = loadThroughTools(context);
  auto *const laterModule = loadThroughDriver();

  CHECK(moduleWord(ModuleOriginSlot, toolsModule) == LoadedByToolsOrigin);
  CHECK(moduleWord(ModuleOriginSlot, driverModule) == 0);
  CHECK(moduleWord(ModuleOriginSlot, laterModule) == 0);

  const auto driverSerial = moduleWord(ModuleSerialSlot, driverModule);
  CHECK(moduleWord(ModuleSerialSlot, toolsModule) == driverSerial + 1);
  CHECK(moduleWord(ModuleSerialSlot, laterModule) == driverSerial + 2);
  CHECK(moduleWord(ModuleFlavourSlot, driverModule) == 2);
  CHECK(moduleWord(ModuleFlavourSlot, toolsModule) == 2);

  uint32_t value = 0;
  for (const auto index : {ModuleOriginSlot, ModuleSerialSlot, ModuleFlavourSlot}) {
    const auto query = slot<ModuleWord>(ModulesId, index);
    CHECK(query(nullptr, &value) == (index == ModuleOriginSlot ? CUDA_ERROR_INVALID_VALUE : CUDA_ERROR_UNKNOWN));
    CHECK(query(unknownModule, &value) == (index == ModuleOriginSlot ? CUDA_ERROR_INVALID_VALUE : CUDA_ERROR_UNKNOWN));
    CHECK(query(toolsModule, nullptr) == (index == ModuleOriginSlot ? CUDA_ERROR_INVALID_VALUE : CUDA_ERROR_UNKNOWN));
  }

  CHECK(cuModuleUnload(driverModule) == CUDA_SUCCESS);
  CHECK(cuModuleUnload(toolsModule) == CUDA_SUCCESS);
  CHECK(cuModuleUnload(laterModule) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA tools context table visits the modules of a context newest first", "[cuda][tools-modules]") {
  auto *const first = createContext();
  auto *const oldest = loadThroughDriver();
  auto *const newest = loadThroughTools(first);
  auto *const second = createContext();
  auto *const other = loadThroughDriver();

  struct Visits {
    std::vector<CUmodule> modules;
    std::vector<uint32_t> sizes;
    std::vector<uint32_t> versions;
  };
  const auto visit = [](CUcontext context) {
    Visits visits;
    ModuleVisit request{
        .reserved = 0,
        .context = context,
        .callback =
            [](void *argument, const ModuleInfo *info) {
              auto &seen = *static_cast<Visits *>(argument);
              seen.modules.push_back(info->module);
              seen.sizes.push_back(info->size);
              seen.versions.push_back(info->version);
            },
        .argument = &visits,
    };
    CHECK(slot<ContextModules>(ContextId, ContextModulesSlot)(&request) == CUDA_SUCCESS);
    return visits;
  };

  const auto ofFirst = visit(first);
  CHECK(ofFirst.modules == std::vector<CUmodule>{newest, oldest});
  CHECK(ofFirst.sizes == std::vector<uint32_t>{0x28, 0x28});
  CHECK(ofFirst.versions == std::vector<uint32_t>{2, 2});
  CHECK(visit(second).modules == std::vector<CUmodule>{other});

  CHECK(slot<ContextModules>(ContextId, ContextModulesSlot)(nullptr) == CUDA_ERROR_INVALID_VALUE);
  ModuleVisit stale{.reserved = 0, .context = reinterpret_cast<CUcontext>(0x10), .callback = nullptr, .argument = nullptr};
  CHECK(slot<ContextModules>(ContextId, ContextModulesSlot)(&stale) == CUDA_ERROR_INVALID_CONTEXT);

  CHECK(cuModuleUnload(oldest) == CUDA_SUCCESS);
  CHECK(cuModuleUnload(newest) == CUDA_SUCCESS);
  CHECK(cuModuleUnload(other) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA tools module table resolves the device functions of a cubin by symbol", "[cuda][tools-modules]") {
  auto *const context = createContext();
  fixture.missingKernel("helper");
  fixture.missingKernel("absent");
  const auto cubin = cubinOf("helper");
  CUmodule module = nullptr;
  REQUIRE(slot<ModuleLoad>(ModulesId, ModuleLoadSlot)(context, &module, cubin.data(), 0, 0, 0) == CUDA_SUCCESS);

  const auto lookup = slot<ModuleFunction>(ModulesId, ModuleFunctionSlot);
  CUfunction helper = nullptr;
  CUfunction again = nullptr;
  CUfunction absent = reinterpret_cast<CUfunction>(0x10);
  CHECK(lookup(context, module, "helper", &helper) == CUDA_SUCCESS);
  CHECK(helper != nullptr);
  CHECK(lookup(context, module, "helper", &again) == CUDA_SUCCESS);
  CHECK(again == helper);
  CHECK(lookup(context, module, "absent", &absent) == CUDA_ERROR_NOT_FOUND);
  CHECK(absent == nullptr);
  CUfunction driver = nullptr;
  CHECK(cuModuleGetFunction(&driver, module, "helper") == CUDA_ERROR_NOT_FOUND);

  uint64_t address = 0;
  CHECK(slot<FunctionAddress>(ModulesId, FunctionAddressSlot)(helper, &address) == CUDA_SUCCESS);
  CHECK(address != 0);

  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
