#include <algorithm>
#include <array>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <functional>
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
constexpr size_t ModuleImageSlot = 62;
constexpr size_t FunctionNameSlot = 5;
constexpr size_t FunctionSymbolSlot = 55;
constexpr size_t SubscriberReleaseSlot = 4;
constexpr size_t SubscriberEnableSlot = 6;
constexpr size_t SubscriberEnableDomainSlot = 7;
constexpr size_t SubscriberEnableAllSlot = 8;
constexpr size_t SubscriberRegisterSlot = 11;
constexpr uint32_t DriverDomain = 2;
constexpr uint32_t ModuleLoadedEvent = 17;
constexpr uint32_t ModuleUnloadingEvent = 18;
constexpr uint32_t LaunchDomain = 3;
constexpr uint32_t LaunchEnterEvent = 1;
constexpr uint32_t LaunchExitEvent = 9;
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
using ModuleImage = CUresult (*)(CUmodule, const void **, size_t *);
using DriverCallback = void (*)(void *, uint32_t, uint32_t, const void *);
using SubscriberRegister = CUresult (*)(uint32_t *, DriverCallback, void *);
using SubscriberRelease = CUresult (*)(uint32_t);
using SubscriberEnable = CUresult (*)(int, uint32_t, uint32_t, uint32_t);
using SubscriberEnableDomain = CUresult (*)(int, uint32_t, uint32_t);
using SubscriberEnableAll = CUresult (*)(int, uint32_t);
using FunctionName = CUresult (*)(CUcontext, CUfunction, const char **);
using FunctionSymbol = CUresult (*)(CUfunction, uint64_t *);

constexpr ExportId SubscriberId{0x2c, 0x8e, 0x0a, 0xd8, 0x07, 0x10, 0xab, 0x4e, 0x90, 0xdd, 0x54, 0x71, 0x9f, 0xe5, 0xf7, 0x4b};

struct ModuleEvent {
  uint32_t size;
  uint32_t padding;
  CUcontext context;
  CUmodule module;
  uint64_t reserved;
  const void *image;
  uint64_t imageSize;
  const char *api;
  uint32_t flavour;
  uint32_t reservedFlavour;
  uint32_t origin;
};

struct ModuleUnloadEvent {
  uint32_t size;
  uint32_t padding;
  CUcontext context;
  CUmodule module;
  uint64_t reserved[2];
};

struct LaunchEnter {
  uint32_t size;
  uint32_t padding;
  CUcontext context;
  CUstream stream;
  CUmodule module;
  CUfunction function;
  uint32_t grid[3];
  uint32_t block[3];
  uint64_t launch;
  uint64_t reserved[4];
  uint64_t reportable;
};

struct LaunchExit {
  uint32_t size;
  uint32_t padding;
  CUcontext context;
  CUstream stream;
  CUmodule module;
  CUfunction function;
  uint64_t launch;
};

struct Delivery {
  uint32_t domain;
  uint32_t callback;
  std::vector<unsigned char> bytes;

  template <class Params> [[nodiscard]] Params as() const {
    REQUIRE(bytes.size() >= sizeof(Params));
    Params params{};
    std::memcpy(&params, bytes.data(), sizeof(Params));
    return params;
  }
};

void record(void *argument, uint32_t domain, uint32_t callback, const void *params) {
  uint32_t size = 0;
  std::memcpy(&size, params, sizeof(size));
  const auto *const bytes = static_cast<const unsigned char *>(params);
  static_cast<std::vector<Delivery> *>(argument)->push_back({.domain = domain, .callback = callback, .bytes = {bytes, bytes + size}});
}

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

constexpr ExportId RuntimeId{0x6b, 0xd5, 0xfb, 0x6c, 0x5b, 0xf4, 0xe7, 0x4a, 0x89, 0x87, 0xd9, 0x39, 0x12, 0xfd, 0x9d, 0xf9};
constexpr size_t RuntimeModuleSlot = 1;

struct FatbinWrapper {
  uint32_t magic;
  uint32_t version;
  const void *data;
  const void *prelinked;
};

using RuntimeModule = CUresult (*)(CUmodule *, const void *);

template <class Value> void store(std::vector<unsigned char> &bytes, size_t offset, Value value) {
  std::memcpy(bytes.data() + offset, &value, sizeof(value));
}

void appendEntry(std::vector<unsigned char> &fatbin, uint16_t kind, uint32_t target, const void *data, size_t size) {
  const auto entry = fatbin.size();
  fatbin.resize(entry + 64 + size);
  store(fatbin, entry, kind);
  store(fatbin, entry + 4, uint32_t{64});
  store(fatbin, entry + 8, static_cast<uint32_t>(size));
  store(fatbin, entry + 16, static_cast<uint32_t>(size));
  store(fatbin, entry + 28, target);
  std::memcpy(fatbin.data() + entry + 64, data, size);
}

std::vector<unsigned char> loadableCubinOf(const std::string &function) {
  auto cubin = cubinOf(function);
  Elf64_Ehdr header;
  std::memcpy(&header, cubin.data(), sizeof(header));
  store(cubin, header.e_shoff + (2 * sizeof(Elf64_Shdr)) + offsetof(Elf64_Shdr, sh_flags), Elf64_Xword{SHF_ALLOC});
  return cubin;
}

std::vector<unsigned char> fatbinOf(const std::vector<unsigned char> &cubin, uint32_t target) {
  std::vector<unsigned char> fatbin(16);
  store(fatbin, 0, uint32_t{0xba55ed50});
  store(fatbin, 6, uint16_t{16});
  appendEntry(fatbin, 1, 0, Image, sizeof(Image) - 1);
  appendEntry(fatbin, 2, target, cubin.data(), cubin.size());
  store(fatbin, 8, static_cast<uint64_t>(fatbin.size() - 16));
  return fatbin;
}

class Subscriber {
  uint32_t id = 0;

public:
  mutable std::vector<Delivery> deliveries;

  Subscriber() { REQUIRE(slot<SubscriberRegister>(SubscriberId, SubscriberRegisterSlot)(&id, record, &deliveries) == CUDA_SUCCESS); }
  Subscriber(const Subscriber &) = delete;
  Subscriber &operator=(const Subscriber &) = delete;
  ~Subscriber() { release(); }

  void enable(uint32_t domain, uint32_t callback, bool on = true) const {
    REQUIRE(slot<SubscriberEnable>(SubscriberId, SubscriberEnableSlot)(on ? 1 : 0, id, domain, callback) == CUDA_SUCCESS);
  }

  void enableDomain(uint32_t domain, bool on) const {
    REQUIRE(slot<SubscriberEnableDomain>(SubscriberId, SubscriberEnableDomainSlot)(on ? 1 : 0, id, domain) == CUDA_SUCCESS);
  }

  void enableAll(bool on) const {
    REQUIRE(slot<SubscriberEnableAll>(SubscriberId, SubscriberEnableAllSlot)(on ? 1 : 0, id) == CUDA_SUCCESS);
  }

  void release() {
    if (id != 0) CHECK(slot<SubscriberRelease>(SubscriberId, SubscriberReleaseSlot)(id) == CUDA_SUCCESS);
    id = 0;
  }
};

} // namespace

TEST_CASE("CUDA driver tools subscriber hears the cubin modules a context loads and unloads", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const auto cubin = cubinOf("kernel");
  Subscriber subscriber;

  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS);
  REQUIRE(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(subscriber.deliveries.empty());

  subscriber.enable(DriverDomain, ModuleLoadedEvent);
  subscriber.enable(DriverDomain, ModuleUnloadingEvent);
  auto callerImage = cubin;
  REQUIRE(cuModuleLoadData(&module, callerImage.data()) == CUDA_SUCCESS);
  std::fill(callerImage.begin(), callerImage.end(), 0);

  REQUIRE(subscriber.deliveries.size() == 1);
  CHECK(subscriber.deliveries[0].domain == DriverDomain);
  CHECK(subscriber.deliveries[0].callback == ModuleLoadedEvent);
  const auto loaded = subscriber.deliveries[0].as<ModuleEvent>();
  CHECK(loaded.size == 0x88);
  CHECK(loaded.context == context);
  CHECK(loaded.module == module);
  CHECK(loaded.reserved == 0);
  REQUIRE(loaded.imageSize == cubin.size());
  REQUIRE(loaded.image != nullptr);
  CHECK(std::memcmp(loaded.image, cubin.data(), cubin.size()) == 0);
  CHECK(std::string(loaded.api) == "cuModuleLoadDataEx");
  CHECK(loaded.flavour == 2);
  CHECK(loaded.origin == 0);

  const auto query = slot<ModuleImage>(ModulesId, ModuleImageSlot);
  const void *image = nullptr;
  size_t size = 0;
  CHECK(query(module, &image, &size) == CUDA_SUCCESS);
  CHECK(image == loaded.image);
  CHECK(size == cubin.size());
  const void *untouchedImage = &size;
  size_t untouchedSize = 7;
  CHECK(query(nullptr, &untouchedImage, &untouchedSize) == CUDA_ERROR_INVALID_VALUE);
  CHECK(query(module, nullptr, &untouchedSize) == CUDA_ERROR_INVALID_VALUE);
  CHECK(query(module, &untouchedImage, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(untouchedImage == &size);
  CHECK(untouchedSize == 7);

  struct Visited {
    CUmodule module = nullptr;
    const void *image = nullptr;
    uint64_t imageSize = 0;
  } visited{.module = module};
  ModuleVisit request{.reserved = 0,
                      .context = context,
                      .callback =
                          [](void *argument, const ModuleInfo *info) {
                            auto &seen = *static_cast<Visited *>(argument);
                            if (info->module != seen.module) return;
                            seen.image = info->image;
                            seen.imageSize = info->imageSize;
                          },
                      .argument = &visited};
  CHECK(slot<ContextModules>(ContextId, ContextModulesSlot)(&request) == CUDA_SUCCESS);
  CHECK(visited.image == image);
  CHECK(visited.imageSize == cubin.size());

  CUmodule toolsModule = nullptr;
  REQUIRE(slot<ModuleLoad>(ModulesId, ModuleLoadSlot)(context, &toolsModule, cubin.data(), 0, 0, 0) == CUDA_SUCCESS);
  REQUIRE(subscriber.deliveries.size() == 2);
  CHECK(subscriber.deliveries[1].as<ModuleEvent>().module == toolsModule);
  CHECK(subscriber.deliveries[1].as<ModuleEvent>().origin == LoadedByToolsOrigin);

  REQUIRE(cuModuleUnload(module) == CUDA_SUCCESS);
  REQUIRE(subscriber.deliveries.size() == 3);
  CHECK(subscriber.deliveries[2].domain == DriverDomain);
  CHECK(subscriber.deliveries[2].callback == ModuleUnloadingEvent);
  const auto unloading = subscriber.deliveries[2].as<ModuleUnloadEvent>();
  CHECK(unloading.size == 0x28);
  CHECK(unloading.context == context);
  CHECK(unloading.module == module);

  subscriber.enable(DriverDomain, ModuleUnloadingEvent, false);
  REQUIRE(cuModuleUnload(toolsModule) == CUDA_SUCCESS);
  CHECK(subscriber.deliveries.size() == 3);

  subscriber.release();
  REQUIRE(cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS);
  CHECK(subscriber.deliveries.size() == 3);
  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA driver tools subscriber enables a domain or every callback at once", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const auto cubin = cubinOf("kernel");
  const Subscriber subscriber;
  const auto cycle = [&] {
    CUmodule module = nullptr;
    REQUIRE(cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS);
    REQUIRE(cuModuleUnload(module) == CUDA_SUCCESS);
  };

  subscriber.enableDomain(DriverDomain, true);
  cycle();
  CHECK(subscriber.deliveries.size() == 2);
  subscriber.enableDomain(DriverDomain, false);
  cycle();
  CHECK(subscriber.deliveries.size() == 2);
  subscriber.enableAll(true);
  cycle();
  CHECK(subscriber.deliveries.size() == 4);
  subscriber.enableAll(false);
  cycle();
  CHECK(subscriber.deliveries.size() == 4);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA driver tools subscriber keeps PTX modules, which have no cubin, to itself", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const Subscriber subscriber;
  subscriber.enable(DriverDomain, ModuleLoadedEvent);
  subscriber.enable(DriverDomain, ModuleUnloadingEvent);
  subscriber.enable(LaunchDomain, LaunchEnterEvent);
  auto *const module = loadThroughDriver();
  CUfunction function = nullptr;
  REQUIRE(cuModuleGetFunction(&function, module, "first") == CUDA_SUCCESS);
  CUdeviceptr allocation = 0;
  REQUIRE(cuMemAlloc_v2(&allocation, 1024) == CUDA_SUCCESS);
  uint32_t seed = 0;
  void *arguments[]{&allocation, &seed};
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0, .geometryOnly = true});
  CHECK(cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr) == CUDA_SUCCESS);
  CHECK(cuCtxSynchronize() == CUDA_SUCCESS);

  const void *image = nullptr;
  size_t size = 0;
  CHECK(slot<ModuleImage>(ModulesId, ModuleImageSlot)(module, &image, &size) == CUDA_ERROR_NOT_SUPPORTED);
  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(subscriber.deliveries.empty());
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA driver tools subscriber is shown the cubin a fatbin offers the device beside its PTX", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const auto cubin = loadableCubinOf("kernel");
  const auto compatible = fatbinOf(cubin, 86);
  const auto newer = fatbinOf(cubin, 90);
  const auto stub = fatbinOf(cubinOf("kernel"), 86);
  const FatbinWrapper wrapper{.magic = 0x466243b1, .version = 1, .data = compatible.data(), .prelinked = nullptr};
  Subscriber subscriber;
  subscriber.enable(DriverDomain, ModuleLoadedEvent);

  const std::array<std::function<CUresult(CUmodule *)>, 3> loads{
      [&](CUmodule *module) { return cuModuleLoadData(module, compatible.data()); },
      [&](CUmodule *module) { return cuModuleLoadDataEx(module, compatible.data(), 0, nullptr, nullptr); },
      [&](CUmodule *module) { return slot<RuntimeModule>(RuntimeId, RuntimeModuleSlot)(module, &wrapper); },
  };
  for (size_t index = 0; index < loads.size(); ++index) {
    CAPTURE(index);
    CUmodule module = nullptr;
    REQUIRE(loads[index](&module) == CUDA_SUCCESS);
    REQUIRE(subscriber.deliveries.size() == index + 1);
    const auto loaded = subscriber.deliveries[index].as<ModuleEvent>();
    CHECK(loaded.module == module);
    REQUIRE(loaded.imageSize == cubin.size());
    CHECK(std::memcmp(loaded.image, cubin.data(), cubin.size()) == 0);
    CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  }

  for (const auto *const unusable : {&newer, &stub}) {
    CUmodule module = nullptr;
    REQUIRE(cuModuleLoadData(&module, unusable->data()) == CUDA_SUCCESS);
    CHECK(subscriber.deliveries.size() == loads.size());
    CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  }
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA driver tools subscriber hears each launch begin and end", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const auto cubin = cubinOf("kernel");
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS);
  CUfunction function = nullptr;
  REQUIRE(cuModuleGetFunction(&function, module, "kernel") == CUDA_SUCCESS);
  Subscriber subscriber;
  subscriber.enable(LaunchDomain, LaunchEnterEvent);
  subscriber.enable(LaunchDomain, LaunchExitEvent);

  CUdeviceptr allocation = 0;
  REQUIRE(cuMemAlloc_v2(&allocation, 1024) == CUDA_SUCCESS);
  uint32_t seed = 0;
  void *arguments[]{&allocation, &seed};
  fixture.expect({.local = 5,
                  .groups = 2,
                  .global = 10,
                  .offset = 0,
                  .seed = 0,
                  .localYz = {6, 7},
                  .groupsYz = {3, 4},
                  .globalYz = {18, 28},
                  .geometryOnly = true});
  for (int repeat = 0; repeat < 2; ++repeat)
    REQUIRE(cuLaunchKernel(function, 2, 3, 4, 5, 6, 7, 0, nullptr, arguments, nullptr) == CUDA_SUCCESS);
  CHECK(cuCtxSynchronize() == CUDA_SUCCESS);

  REQUIRE(subscriber.deliveries.size() == 4);
  for (const size_t first : {size_t{0}, size_t{2}}) {
    CHECK(subscriber.deliveries[first].domain == LaunchDomain);
    CHECK(subscriber.deliveries[first].callback == LaunchEnterEvent);
    CHECK(subscriber.deliveries[first + 1].callback == LaunchExitEvent);
    const auto enter = subscriber.deliveries[first].as<LaunchEnter>();
    CHECK(enter.size == 0x100);
    CHECK(enter.reportable == 1);
    CHECK(enter.context == context);
    CHECK(enter.module == module);
    CHECK(enter.function == function);
    CHECK(std::vector<uint32_t>(enter.grid, enter.grid + 3) == std::vector<uint32_t>{2, 3, 4});
    CHECK(std::vector<uint32_t>(enter.block, enter.block + 3) == std::vector<uint32_t>{5, 6, 7});
    const auto exit = subscriber.deliveries[first + 1].as<LaunchExit>();
    CHECK(exit.size == 0x40);
    CHECK(exit.context == context);
    CHECK(exit.module == module);
    CHECK(exit.function == function);
    CHECK(exit.launch == enter.launch);
  }
  CHECK(subscriber.deliveries[2].as<LaunchEnter>().launch == subscriber.deliveries[0].as<LaunchEnter>().launch + 1);

  CHECK(cuMemFree_v2(allocation) == CUDA_SUCCESS);
  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

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

TEST_CASE("CUDA tools module table names a function and its cubin symbol", "[cuda][tools-modules]") {
  auto *const context = createContext();
  const auto cubin = cubinOf("kernel");
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS);
  CUfunction function = nullptr;
  REQUIRE(cuModuleGetFunction(&function, module, "kernel") == CUDA_SUCCESS);

  const auto naming = slot<FunctionName>(ModulesId, FunctionNameSlot);
  const char *name = nullptr;
  REQUIRE(naming(context, function, &name) == CUDA_SUCCESS);
  REQUIRE(name != nullptr);
  CHECK(std::string(name) == "kernel");
  CHECK(naming(nullptr, function, &name) == CUDA_SUCCESS);
  CHECK(naming(context, nullptr, &name) == CUDA_ERROR_INVALID_VALUE);
  CHECK(naming(context, function, nullptr) == CUDA_ERROR_INVALID_VALUE);

  const auto symbol = slot<FunctionSymbol>(ModulesId, FunctionSymbolSlot);
  uint64_t index = 0;
  CHECK(symbol(function, &index) == CUDA_SUCCESS);
  CHECK(index == 1);
  CHECK(symbol(nullptr, &index) == CUDA_ERROR_UNKNOWN);
  CHECK(symbol(function, nullptr) == CUDA_ERROR_UNKNOWN);

  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
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
