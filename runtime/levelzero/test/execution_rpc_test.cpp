#include <ze_api.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

#include <array>
#include <cassert>
#include <cstdint>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

namespace {

constexpr uint32_t request_magic = 0x474d4531;
constexpr uint32_t response_magic = 0x474d4631;
constexpr uint32_t protocol_version = 3;
constexpr uint64_t program_id = 91;
constexpr uint64_t completion_id = 92;
constexpr char token[] = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

uint32_t read_u32(const uint8_t *input) {
  return (static_cast<uint32_t>(input[0]) << 24) | (static_cast<uint32_t>(input[1]) << 16) |
         (static_cast<uint32_t>(input[2]) << 8) | static_cast<uint32_t>(input[3]);
}

uint64_t read_u64(const uint8_t *input) {
  return (static_cast<uint64_t>(read_u32(input)) << 32) | read_u32(input + 4);
}

void append_u32(std::vector<uint8_t> &output, uint32_t value) {
  output.push_back(static_cast<uint8_t>(value >> 24));
  output.push_back(static_cast<uint8_t>(value >> 16));
  output.push_back(static_cast<uint8_t>(value >> 8));
  output.push_back(static_cast<uint8_t>(value));
}

void append_u64(std::vector<uint8_t> &output, uint64_t value) {
  append_u32(output, static_cast<uint32_t>(value >> 32));
  append_u32(output, static_cast<uint32_t>(value));
}

void receive_all(int socket, void *output, size_t size) {
  auto *bytes = static_cast<uint8_t *>(output);
  while (size != 0) {
    const auto count = recv(socket, bytes, size, 0);
    assert(count > 0);
    bytes += count;
    size -= static_cast<size_t>(count);
  }
}

void send_all(int socket, const void *input, size_t size) {
  const auto *bytes = static_cast<const uint8_t *>(input);
  while (size != 0) {
    const auto count = send(socket, bytes, size, MSG_NOSIGNAL);
    assert(count > 0);
    bytes += count;
    size -= static_cast<size_t>(count);
  }
}

void respond(int socket, const std::vector<uint8_t> &payload = {}) {
  std::vector<uint8_t> header;
  append_u32(header, response_magic);
  append_u32(header, protocol_version);
  append_u32(header, 0);
  append_u32(header, 0);
  append_u32(header, static_cast<uint32_t>(payload.size()));
  send_all(socket, header.data(), header.size());
  if (!payload.empty()) send_all(socket, payload.data(), payload.size());
}

struct MockServer {
  MockServer() {
    listener = socket(AF_INET, SOCK_STREAM, 0);
    assert(listener >= 0);
    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    address.sin_port = 0;
    assert(bind(listener, reinterpret_cast<const sockaddr *>(&address), sizeof(address)) == 0);
    assert(listen(listener, 1) == 0);
    socklen_t size = sizeof(address);
    assert(getsockname(listener, reinterpret_cast<sockaddr *>(&address), &size) == 0);
    port = ntohs(address.sin_port);
    worker = std::thread([this] { serve(); });
  }

  ~MockServer() {
    if (listener >= 0) close(listener);
    if (worker.joinable()) worker.join();
  }

  uint16_t port = 0;
  std::vector<uint8_t> allocation;
  uint64_t allocation_id = 0;

private:
  void serve() {
    const int connection = accept(listener, nullptr, nullptr);
    assert(connection >= 0);
    bool running = true;
    while (running) {
      std::array<uint8_t, 96> header{};
      receive_all(connection, header.data(), header.size());
      assert(read_u32(header.data()) == request_magic);
      assert(read_u32(header.data() + 4) == protocol_version);
      assert(std::memcmp(header.data() + 8, token, 64) == 0);
      assert(read_u64(header.data() + 72) != 0 || read_u64(header.data() + 80) != 0);
      const uint32_t operation = read_u32(header.data() + 88);
      std::vector<uint8_t> payload(read_u32(header.data() + 92));
      if (!payload.empty()) receive_all(connection, payload.data(), payload.size());

      switch (operation) {
      case 0: assert(payload.empty()); respond(connection); break;
      case 1: register_allocation(payload); respond(connection); break;
      case 2:
        assert(read_u64(payload.data()) == allocation_id);
        respond(connection);
        running = false;
        break;
      case 3: write_allocation(payload); respond(connection); break;
      case 4: read_allocation(connection, payload); break;
      case 5: load_program(connection, payload); break;
      case 6: assert(read_u64(payload.data()) == program_id); respond(connection); break;
      case 7: launch(connection, payload); break;
      case 8: query_completion(connection, payload); break;
      case 9: assert(read_u64(payload.data()) == completion_id); respond(connection); break;
      default: assert(false);
      }
    }
    close(connection);
  }

  void register_allocation(const std::vector<uint8_t> &payload) {
    assert(payload.size() == 20);
    allocation_id = read_u64(payload.data());
    assert(allocation_id != 0);
    const auto process_namespace = static_cast<uint64_t>(static_cast<uint32_t>(getpid())) & 0x7fffffffULL;
    assert(allocation_id >> 32 == process_namespace);
    assert(read_u32(payload.data() + 8) == 1);
    allocation.resize(read_u64(payload.data() + 12));
  }

  void write_allocation(const std::vector<uint8_t> &payload) {
    assert(read_u64(payload.data()) == allocation_id);
    const auto offset = read_u64(payload.data() + 8);
    const auto size = read_u32(payload.data() + 16);
    assert(offset + size <= allocation.size());
    assert(payload.size() == 20 + size);
    std::memcpy(allocation.data() + offset, payload.data() + 20, size);
  }

  void read_allocation(int connection, const std::vector<uint8_t> &payload) {
    assert(read_u64(payload.data()) == allocation_id);
    const auto offset = read_u64(payload.data() + 8);
    const auto size = read_u32(payload.data() + 16);
    assert(offset + size <= allocation.size());
    respond(connection, std::vector<uint8_t>(allocation.begin() + offset, allocation.begin() + offset + size));
  }

  void load_program(int connection, const std::vector<uint8_t> &payload) {
    assert(read_u32(payload.data()) == 3);
    assert(read_u32(payload.data() + 4) == 4);
    assert(payload[8] == 3 && payload[9] == 2 && payload[10] == 35 && payload[11] == 7);
    std::vector<uint8_t> response;
    append_u64(response, program_id);
    respond(connection, response);
  }

  void launch(int connection, const std::vector<uint8_t> &payload) {
    size_t cursor = 0;
    assert(read_u64(payload.data()) == program_id);
    cursor += 8;
    const uint32_t name_size = read_u32(payload.data() + cursor);
    cursor += 4;
    assert(std::string(reinterpret_cast<const char *>(payload.data() + cursor), name_size) == "write");
    cursor += name_size;
    assert(read_u32(payload.data() + cursor) == 4);
    assert(read_u32(payload.data() + cursor + 4) == 1);
    assert(read_u32(payload.data() + cursor + 8) == 1);
    assert(read_u32(payload.data() + cursor + 12) == 2);
    assert(read_u32(payload.data() + cursor + 16) == 1);
    assert(read_u32(payload.data() + cursor + 20) == 1);
    assert(read_u32(payload.data() + cursor + 24) == 8);
    assert(read_u32(payload.data() + cursor + 28) == 1);
    assert(read_u32(payload.data() + cursor + 32) == 1);
    assert(read_u32(payload.data() + cursor + 36) == 0);
    assert(read_u32(payload.data() + cursor + 40) == 0);
    cursor += 44;
    assert(read_u32(payload.data() + cursor) == 3);
    cursor += 4;
    assert(read_u32(payload.data() + cursor) == 2);
    assert(read_u64(payload.data() + cursor + 4) == allocation_id);
    assert(read_u64(payload.data() + cursor + 12) == 4);
    cursor += 20;
    assert(read_u32(payload.data() + cursor) == 1);
    assert(read_u32(payload.data() + cursor + 4) == 4);
    const auto *value = payload.data() + cursor + 8;
    cursor += 12;
    assert(read_u32(payload.data() + cursor) == 3);
    assert(read_u64(payload.data() + cursor + 4) == 64);
    cursor += 12;
    assert(cursor == payload.size());
    std::memcpy(allocation.data() + 4, value, sizeof(uint32_t));
    std::vector<uint8_t> response;
    append_u64(response, completion_id);
    respond(connection, response);
  }

  void query_completion(int connection, const std::vector<uint8_t> &payload) {
    assert(read_u64(payload.data()) == completion_id);
    std::vector<uint8_t> response;
    append_u32(response, 2);
    respond(connection, response);
  }

  int listener = -1;
  std::thread worker;
};

ze_device_handle_t gemstone_device(ze_driver_handle_t *selected_driver) {
  uint32_t driver_count = 0;
  assert(zeDriverGet(&driver_count, nullptr) == ZE_RESULT_SUCCESS);
  std::vector<ze_driver_handle_t> drivers(driver_count);
  assert(zeDriverGet(&driver_count, drivers.data()) == ZE_RESULT_SUCCESS);
  for (const auto driver : drivers) {
    uint32_t device_count = 0;
    assert(zeDeviceGet(driver, &device_count, nullptr) == ZE_RESULT_SUCCESS);
    std::vector<ze_device_handle_t> devices(device_count);
    assert(zeDeviceGet(driver, &device_count, devices.data()) == ZE_RESULT_SUCCESS);
    for (const auto device : devices) {
      ze_device_properties_t properties{};
      properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
      assert(zeDeviceGetProperties(device, &properties) == ZE_RESULT_SUCCESS);
      if (std::strstr(properties.name, "Gemstone") != nullptr) {
        *selected_driver = driver;
        return device;
      }
    }
  }
  return nullptr;
}

} // namespace

int main() {
  MockServer server;
  const auto port = std::to_string(server.port);
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", token, 1) == 0);
  assert(zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS);

  ze_driver_handle_t driver = nullptr;
  const auto device = gemstone_device(&driver);
  assert(device != nullptr && driver != nullptr);
  const char *driver_error = nullptr;
  assert(zeDriverGetLastErrorDescription(driver, &driver_error) == ZE_RESULT_SUCCESS);
  assert(driver_error != nullptr && driver_error[0] != '\0');
  ze_context_desc_t context_desc{};
  context_desc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  assert(zeContextCreate(driver, &context_desc, &context) == ZE_RESULT_SUCCESS);

  ze_device_mem_alloc_desc_t allocation_desc{};
  allocation_desc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *memory = nullptr;
  assert(zeMemAllocDevice(context, &allocation_desc, 16, 8, device, &memory) == ZE_RESULT_SUCCESS);
  std::memset(memory, 0, 16);

  const uint8_t image[] = {3, 2, 35, 7};
  ze_module_desc_t module_desc{};
  module_desc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  module_desc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  module_desc.inputSize = sizeof(image);
  module_desc.pInputModule = image;
  ze_module_handle_t module = nullptr;
  ze_module_build_log_handle_t log = nullptr;
  assert(zeModuleCreate(context, device, &module_desc, &module, &log) == ZE_RESULT_SUCCESS);
  ze_module_properties_t module_properties{};
  module_properties.stype = ZE_STRUCTURE_TYPE_MODULE_PROPERTIES;
  assert(zeModuleGetProperties(module, &module_properties) == ZE_RESULT_SUCCESS);
  assert(module_properties.flags == 0);
  assert(zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS);

  ze_kernel_desc_t kernel_desc{};
  kernel_desc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernel_desc.pKernelName = "write";
  ze_kernel_handle_t kernel = nullptr;
  assert(zeKernelCreate(module, &kernel_desc, &kernel) == ZE_RESULT_SUCCESS);
  ze_kernel_preferred_group_size_properties_t preferred{};
  preferred.stype = ZE_STRUCTURE_TYPE_KERNEL_PREFERRED_GROUP_SIZE_PROPERTIES;
  ze_kernel_properties_t kernel_properties{};
  kernel_properties.stype = ZE_STRUCTURE_TYPE_KERNEL_PROPERTIES;
  kernel_properties.pNext = &preferred;
  assert(zeKernelGetProperties(kernel, &kernel_properties) == ZE_RESULT_SUCCESS);
  assert(kernel_properties.maxSubgroupSize == 32);
  assert(kernel_properties.maxNumSubgroups == 128);
  assert(preferred.preferredMultiple == 8);
  uint32_t suggested_x = 0;
  uint32_t suggested_y = 0;
  uint32_t suggested_z = 0;
  assert(zeKernelSuggestGroupSize(kernel, 521, 7, 3, &suggested_x, &suggested_y, &suggested_z) ==
         ZE_RESULT_SUCCESS);
  assert(suggested_x > 0 && 521 % suggested_x == 0);
  assert(suggested_y > 0 && 7 % suggested_y == 0);
  assert(suggested_z > 0 && 3 % suggested_z == 0);
  assert(static_cast<uint64_t>(suggested_x) * suggested_y * suggested_z <= 1024);
  assert(zeKernelSetGroupSize(kernel, 4, 1, 1) == ZE_RESULT_SUCCESS);
  void *interior = static_cast<uint8_t *>(memory) + 4;
  const uint32_t expected = 0x78563412;
  assert(zeKernelSetArgumentValue(kernel, 0, sizeof(interior), &interior) == ZE_RESULT_SUCCESS);
  assert(zeKernelSetArgumentValue(kernel, 1, sizeof(expected), &expected) == ZE_RESULT_SUCCESS);
  assert(zeKernelSetArgumentValue(kernel, 2, 64, nullptr) == ZE_RESULT_SUCCESS);

  ze_command_queue_desc_t queue_desc{};
  queue_desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queue_desc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  assert(zeCommandQueueCreate(context, device, &queue_desc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t list_desc{};
  list_desc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  assert(zeCommandListCreate(context, device, &list_desc, &list) == ZE_RESULT_SUCCESS);
  const ze_group_count_t groups{2, 1, 1};
  assert(zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  assert(zeMemFree(context, memory) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  assert(zeKernelDestroy(kernel) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  assert(zeModuleDestroy(module) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  const uint32_t changed_after_recording = 0xabcdef01;
  assert(zeKernelSetArgumentValue(kernel, 1, sizeof(changed_after_recording), &changed_after_recording) == ZE_RESULT_SUCCESS);
  assert(zeKernelSetGroupSize(kernel, 8, 1, 1) == ZE_RESULT_SUCCESS);
  assert(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS);
  assert(*reinterpret_cast<uint32_t *>(static_cast<uint8_t *>(memory) + 4) == expected);

  assert(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  assert(zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS);
  assert(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  assert(zeMemFree(context, memory) == ZE_RESULT_SUCCESS);
  assert(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
  return 0;
}
