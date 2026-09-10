#define _POSIX_C_SOURCE 200112L
#include "gemstone_process_identity.h"

#include "gemstone_execution_rpc.h"

#include <arpa/inet.h>
#include <errno.h>
#include <netdb.h>
#include <netinet/tcp.h>
#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <unistd.h>

#include <limits>
#include <chrono>
#include <new>
#include <string>
#include <vector>

namespace {

constexpr uint32_t kRequestMagic = UINT32_C(0x474d4531);
constexpr uint32_t kResponseMagic = UINT32_C(0x474d4631);
constexpr uint32_t kProtocolVersion = 3;
constexpr uint32_t kTokenSize = 64;
constexpr uint32_t kResponseHeaderSize = 20;
constexpr uint32_t kMaximumPayload = 128U * 1024U * 1024U;
constexpr uint32_t kMaximumMessage = 8192;
constexpr int kSocketTimeoutSeconds = 300;

enum Operation : uint32_t {
  Attest = 0,
  RegisterAllocation = 1,
  ReleaseAllocation = 2,
  WriteAllocation = 3,
  ReadAllocation = 4,
  LoadProgram = 5,
  UnloadProgram = 6,
  Launch = 7,
  QueryCompletion = 8,
  ReleaseCompletion = 9,
  CompletionDirtyRanges = 10,
  LaunchWithRelocations = 11,
  LaunchWithGeometry = 12,
  LaunchWithGeometryAndRelocations = 13,
  CompletionPointerRelocations = 14,
  WriteProgramGlobal = 15,
  BindProgramGlobal = 16,
  RegisterAllocationAddress = 17
};

void put_u32(std::vector<uint8_t> &output, uint32_t value) {
  output.push_back(static_cast<uint8_t>(value >> 24));
  output.push_back(static_cast<uint8_t>(value >> 16));
  output.push_back(static_cast<uint8_t>(value >> 8));
  output.push_back(static_cast<uint8_t>(value));
}

void put_u64(std::vector<uint8_t> &output, uint64_t value) {
  put_u32(output, static_cast<uint32_t>(value >> 32));
  put_u32(output, static_cast<uint32_t>(value));
}

uint32_t get_u32(const uint8_t *input) {
  return (static_cast<uint32_t>(input[0]) << 24) |
         (static_cast<uint32_t>(input[1]) << 16) |
         (static_cast<uint32_t>(input[2]) << 8) |
         static_cast<uint32_t>(input[3]);
}

uint64_t get_u64(const uint8_t *input) {
  return (static_cast<uint64_t>(get_u32(input)) << 32) | get_u32(input + 4);
}

bool valid_token(const char *token) {
  if (token == nullptr || strlen(token) != kTokenSize) return false;
  for (uint32_t index = 0; index < kTokenSize; ++index) {
    const char value = token[index];
    if (!((value >= '0' && value <= '9') || (value >= 'a' && value <= 'f'))) return false;
  }
  return true;
}

int send_all(int fd, const uint8_t *data, size_t size) {
  while (size != 0) {
    const ssize_t sent = send(fd, data, size, MSG_NOSIGNAL);
    if (sent < 0 && errno == EINTR) continue;
    if (sent <= 0) return -1;
    data += static_cast<size_t>(sent);
    size -= static_cast<size_t>(sent);
  }
  return 0;
}

int receive_all(int fd, uint8_t *data, size_t size) {
  while (size != 0) {
    const ssize_t received = recv(fd, data, size, 0);
    if (received < 0 && errno == EINTR) continue;
    if (received <= 0) return -1;
    data += static_cast<size_t>(received);
    size -= static_cast<size_t>(received);
  }
  return 0;
}

} // namespace

struct gemstone_execution_client {
  int fd = -1;
  bool established = false;
  bool poisoned = false;
  pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
  char token[65]{};
  uint64_t session_high = 0;
  uint64_t session_low = 0;
  std::string error;
};

namespace {

std::pair<uint64_t, uint64_t> process_session() {
  return {static_cast<uint64_t>(static_cast<uint32_t>(getpid())), gemstone_process_incarnation()};
}

void disconnect(gemstone_execution_client *client) {
  if (client->fd >= 0) close(client->fd);
  client->fd = -1;
}

void lose_transport(gemstone_execution_client *client) {
  disconnect(client);
  if (client->established) client->poisoned = true;
}

int fail(gemstone_execution_client *client, const std::string &message) {
  client->error = message;
  return -1;
}

int connect_server(gemstone_execution_client *client) {
  if (client->fd >= 0) return 0;
  if (client->poisoned) return fail(client, "execution session was lost; create a new client and rebuild its state");
  const char *port = getenv("GEMSTONE_RPC_PORT");
  if (port == nullptr || port[0] == '\0') return fail(client, "GEMSTONE_RPC_PORT is not set");

  addrinfo hints{};
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;
  addrinfo *addresses = nullptr;
  if (getaddrinfo("127.0.0.1", port, &hints, &addresses) != 0)
    return fail(client, "cannot resolve the Gemstone RPC endpoint");

  int fd = -1;
  for (const addrinfo *address = addresses; address != nullptr; address = address->ai_next) {
    fd = socket(address->ai_family, address->ai_socktype, address->ai_protocol);
    if (fd < 0) continue;
    if (connect(fd, address->ai_addr, address->ai_addrlen) == 0) break;
    close(fd);
    fd = -1;
  }
  freeaddrinfo(addresses);
  if (fd < 0) return fail(client, "cannot connect to the Gemstone RPC endpoint");
  const timeval timeout{kSocketTimeoutSeconds, 0};
  const int no_delay = 1;
  if (setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, &no_delay, sizeof(no_delay)) != 0 ||
      setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) != 0 ||
      setsockopt(fd, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) != 0) {
    close(fd);
    return fail(client, "cannot configure the Gemstone RPC socket");
  }
  client->fd = fd;
  client->established = true;
  return 0;
}

int exchange(gemstone_execution_client *client, Operation operation,
             const std::vector<uint8_t> &payload, std::vector<uint8_t> &response) {
  if (payload.size() > kMaximumPayload) return fail(client, "execution request exceeds the payload limit");
  struct MutexGuard {
    explicit MutexGuard(pthread_mutex_t *value) : mutex(value) { pthread_mutex_lock(mutex); }
    ~MutexGuard() { pthread_mutex_unlock(mutex); }
    pthread_mutex_t *mutex;
  } guard(&client->mutex);
  client->error.clear();
  if (connect_server(client) != 0) return -1;

  std::vector<uint8_t> header;
  header.reserve(96);
  put_u32(header, kRequestMagic);
  put_u32(header, kProtocolVersion);
  header.insert(header.end(), client->token, client->token + kTokenSize);
  put_u64(header, client->session_high);
  put_u64(header, client->session_low);
  put_u32(header, static_cast<uint32_t>(operation));
  put_u32(header, static_cast<uint32_t>(payload.size()));
  if (send_all(client->fd, header.data(), header.size()) != 0 ||
      (!payload.empty() && send_all(client->fd, payload.data(), payload.size()) != 0)) {
    lose_transport(client);
    return fail(client, "execution request transport failed");
  }

  uint8_t response_header[kResponseHeaderSize];
  if (receive_all(client->fd, response_header, sizeof(response_header)) != 0) {
    lose_transport(client);
    return fail(client, "execution response transport failed");
  }
  if (get_u32(response_header) != kResponseMagic || get_u32(response_header + 4) != kProtocolVersion) {
    lose_transport(client);
    return fail(client, "execution response header is invalid");
  }
  const uint32_t status = get_u32(response_header + 8);
  const uint32_t message_size = get_u32(response_header + 12);
  const uint32_t payload_size = get_u32(response_header + 16);
  if (message_size > kMaximumMessage || payload_size > kMaximumPayload) {
    lose_transport(client);
    return fail(client, "execution response exceeds the payload limit");
  }
  std::vector<uint8_t> message(message_size);
  response.resize(payload_size);
  if ((!message.empty() && receive_all(client->fd, message.data(), message.size()) != 0) ||
      (!response.empty() && receive_all(client->fd, response.data(), response.size()) != 0)) {
    lose_transport(client);
    return fail(client, "execution response body is truncated");
  }
  if (status != 0) {
    const std::string detail = message.empty() ? "execution request failed" : std::string(message.begin(), message.end());
    return fail(client, detail);
  }
  return 0;
}

std::vector<uint8_t> identity_payload(uint64_t id) {
  std::vector<uint8_t> result;
  result.reserve(8);
  put_u64(result, id);
  return result;
}

void append_dimensions(std::vector<uint8_t> &payload, gemstone_dimensions dimensions) {
  put_u32(payload, dimensions.x);
  put_u32(payload, dimensions.y);
  put_u32(payload, dimensions.z);
}

bool append_kernel_argument(std::vector<uint8_t> &payload, const gemstone_kernel_argument &argument) {
  put_u32(payload, static_cast<uint32_t>(argument.kind));
  if (argument.kind == GEMSTONE_ARGUMENT_SCALAR) {
    if (argument.scalar_size != 0 && argument.scalar_bytes == nullptr) return false;
    put_u32(payload, argument.scalar_size);
    if (argument.scalar_size != 0)
      payload.insert(payload.end(), argument.scalar_bytes, argument.scalar_bytes + argument.scalar_size);
  } else if (argument.kind == GEMSTONE_ARGUMENT_ALLOCATION) {
    if (argument.allocation_id == 0) return false;
    put_u64(payload, argument.allocation_id);
    put_u64(payload, argument.allocation_offset);
  } else if (argument.kind == GEMSTONE_ARGUMENT_LOCAL) {
    put_u64(payload, argument.local_size);
  } else return false;
  return true;
}

int launch_request(gemstone_execution_client *client, Operation operation, uint64_t program_id,
                   const char *kernel_name, gemstone_dimensions group_size, gemstone_dimensions group_count,
                   gemstone_dimensions global_size, uint32_t work_dim, gemstone_dimensions global_offset,
                   uint32_t private_segment_bytes, uint32_t group_segment_bytes,
                   const gemstone_kernel_argument *arguments, uint32_t argument_count,
                   const gemstone_pointer_relocation *relocations, uint32_t relocation_count,
                   uint64_t *completion_id) {
  const bool has_geometry = operation == LaunchWithGeometry || operation == LaunchWithGeometryAndRelocations;
  const bool has_relocations = operation == LaunchWithRelocations || operation == LaunchWithGeometryAndRelocations;
  if (client == nullptr || program_id == 0 || kernel_name == nullptr || kernel_name[0] == '\0' ||
      completion_id == nullptr || (argument_count != 0 && arguments == nullptr) ||
      (relocation_count != 0 && relocations == nullptr) || (has_geometry && (work_dim < 1 || work_dim > 3))) return -1;
  const size_t name_size = strlen(kernel_name);
  if (name_size > 4096 || name_size > std::numeric_limits<uint32_t>::max()) return -1;
  // Check the whole wire size before touching scalar payloads or relocation entries.
  uint64_t encoded_size = 8U + 4U + name_size + 36U + (has_geometry ? 16U : 0U) + 8U + 4U;
  if (has_relocations) encoded_size += 4ULL + 40ULL * relocation_count;
  if (encoded_size > kMaximumPayload || argument_count > (kMaximumPayload - encoded_size) / 8U)
    return fail(client, "execution request exceeds the payload limit");
  for (uint32_t index = 0; index < argument_count; ++index) {
    const auto &argument = arguments[index];
    switch (argument.kind) {
      case GEMSTONE_ARGUMENT_SCALAR: encoded_size += 8ULL + argument.scalar_size; break;
      case GEMSTONE_ARGUMENT_ALLOCATION: encoded_size += 20U; break;
      case GEMSTONE_ARGUMENT_LOCAL: encoded_size += 12U; break;
      default: return fail(client, "invalid execution argument kind");
    }
    if (encoded_size > kMaximumPayload) return fail(client, "execution request exceeds the payload limit");
  }
  std::vector<uint8_t> payload = identity_payload(program_id);
  put_u32(payload, static_cast<uint32_t>(name_size));
  payload.insert(payload.end(), kernel_name, kernel_name + name_size);
  append_dimensions(payload, group_size);
  append_dimensions(payload, group_count);
  append_dimensions(payload, global_size);
  if (has_geometry) {
    put_u32(payload, work_dim);
    append_dimensions(payload, global_offset);
  }
  put_u32(payload, private_segment_bytes);
  put_u32(payload, group_segment_bytes);
  if (has_relocations) {
    put_u32(payload, relocation_count);
    for (uint32_t index = 0; index < relocation_count; ++index) {
      const auto &relocation = relocations[index];
      if ((relocation.source_allocation_id == 0 &&
           relocation.source_argument_index == std::numeric_limits<uint32_t>::max()) ||
          (relocation.source_allocation_id != 0 &&
           relocation.source_argument_index != std::numeric_limits<uint32_t>::max()) ||
          relocation.target_allocation_id == 0) return -1;
      put_u64(payload, relocation.source_allocation_id);
      put_u32(payload, relocation.source_argument_index);
      put_u32(payload, 0);
      put_u64(payload, relocation.source_offset);
      put_u64(payload, relocation.target_allocation_id);
      put_u64(payload, relocation.target_offset);
    }
  }
  put_u32(payload, argument_count);
  for (uint32_t index = 0; index < argument_count; ++index)
    if (!append_kernel_argument(payload, arguments[index])) return -1;
  std::vector<uint8_t> response;
  if (exchange(client, operation, payload, response) != 0) return -1;
  if (response.size() != 8) return fail(client, "launch returned an invalid completion identity");
  *completion_id = get_u64(response.data());
  return *completion_id == 0 ? fail(client, "launch returned the sentinel completion identity") : 0;
}

} // namespace

extern "C" gemstone_execution_client *gemstone_execution_client_create_from_environment(void) {
  const char *token = getenv("GEMSTONE_RPC_TOKEN");
  if (!valid_token(token)) return nullptr;
  const auto session = process_session();
  if (session.second == 0) return nullptr;
  auto *client = new (std::nothrow) gemstone_execution_client();
  if (client == nullptr) return nullptr;
  memcpy(client->token, token, kTokenSize);
  client->session_high = session.first;
  client->session_low = session.second;
  return client;
}

extern "C" void gemstone_execution_client_destroy(gemstone_execution_client *client) {
  if (client == nullptr) return;
  disconnect(client);
  pthread_mutex_destroy(&client->mutex);
  delete client;
}

extern "C" const char *gemstone_execution_client_error(const gemstone_execution_client *client) {
  return client == nullptr ? "execution client is unavailable" : client->error.c_str();
}

extern "C" int gemstone_execution_attest(gemstone_execution_client *client) {
  if (client == nullptr) return -1;
  std::vector<uint8_t> response;
  return exchange(client, Attest, {}, response);
}

extern "C" int gemstone_execution_register_allocation(gemstone_execution_client *client, uint64_t allocation_id,
                                                        uint64_t size, gemstone_allocation_kind kind) {
  if (client == nullptr || allocation_id == 0) return -1;
  std::vector<uint8_t> payload;
  payload.reserve(20);
  put_u64(payload, allocation_id);
  put_u32(payload, static_cast<uint32_t>(kind));
  put_u64(payload, size);
  std::vector<uint8_t> response;
  return exchange(client, RegisterAllocation, payload, response);
}

extern "C" int gemstone_execution_register_allocation_address(gemstone_execution_client *client, uint64_t allocation_id,
                                                               uint64_t native_base) {
  if (client == nullptr || allocation_id == 0 || native_base == 0) return -1;
  std::vector<uint8_t> payload = identity_payload(allocation_id);
  put_u64(payload, native_base);
  std::vector<uint8_t> response;
  return exchange(client, RegisterAllocationAddress, payload, response);
}

extern "C" int gemstone_execution_release_allocation(gemstone_execution_client *client, uint64_t allocation_id) {
  if (client == nullptr || allocation_id == 0) return -1;
  std::vector<uint8_t> response;
  return exchange(client, ReleaseAllocation, identity_payload(allocation_id), response);
}

extern "C" int gemstone_execution_write_allocation(gemstone_execution_client *client, uint64_t allocation_id,
                                                     uint64_t offset, const void *bytes, uint32_t size) {
  if (client == nullptr || allocation_id == 0 || (size != 0 && bytes == nullptr)) return -1;
  if (size > kMaximumPayload - 20U) return fail(client, "execution request exceeds the payload limit");
  std::vector<uint8_t> payload;
  payload.reserve(20ULL + size);
  put_u64(payload, allocation_id);
  put_u64(payload, offset);
  put_u32(payload, size);
  const auto *input = static_cast<const uint8_t *>(bytes);
  if (size != 0) payload.insert(payload.end(), input, input + size);
  std::vector<uint8_t> response;
  return exchange(client, WriteAllocation, payload, response);
}

extern "C" int gemstone_execution_read_allocation(gemstone_execution_client *client, uint64_t allocation_id,
                                                    uint64_t offset, void *bytes, uint32_t size) {
  if (client == nullptr || allocation_id == 0 || (size != 0 && bytes == nullptr)) return -1;
  if (size > kMaximumPayload) return fail(client, "execution response exceeds the payload limit");
  std::vector<uint8_t> payload = identity_payload(allocation_id);
  put_u64(payload, offset);
  put_u32(payload, size);
  std::vector<uint8_t> response;
  if (exchange(client, ReadAllocation, payload, response) != 0) return -1;
  if (response.size() != size) return fail(client, "allocation read returned an unexpected byte count");
  if (size != 0) memcpy(bytes, response.data(), size);
  return 0;
}

extern "C" int gemstone_execution_load_program(gemstone_execution_client *client, gemstone_program_format format,
                                                 const void *image, uint32_t image_size, uint64_t *program_id) {
  if (client == nullptr || program_id == nullptr || (image_size != 0 && image == nullptr)) return -1;
  if (image_size > kMaximumPayload - 8U) return fail(client, "execution request exceeds the payload limit");
  std::vector<uint8_t> payload;
  payload.reserve(8ULL + image_size);
  put_u32(payload, static_cast<uint32_t>(format));
  put_u32(payload, image_size);
  const auto *bytes = static_cast<const uint8_t *>(image);
  if (image_size != 0) payload.insert(payload.end(), bytes, bytes + image_size);
  std::vector<uint8_t> response;
  if (exchange(client, LoadProgram, payload, response) != 0) return -1;
  if (response.size() != 8) return fail(client, "program load returned an invalid identity");
  *program_id = get_u64(response.data());
  return *program_id == 0 ? fail(client, "program load returned the sentinel identity") : 0;
}

extern "C" int gemstone_execution_unload_program(gemstone_execution_client *client, uint64_t program_id) {
  if (client == nullptr || program_id == 0) return -1;
  std::vector<uint8_t> response;
  return exchange(client, UnloadProgram, identity_payload(program_id), response);
}

extern "C" int gemstone_execution_write_program_global(gemstone_execution_client *client, uint64_t program_id,
                                                          const char *name, uint64_t offset, const void *bytes, uint32_t size) {
  if (client == nullptr || program_id == 0 || name == nullptr || name[0] == '\0' || strlen(name) > 4096 ||
      (size != 0 && bytes == nullptr))
    return -1;
  if (size > kMaximumPayload - 24U - strlen(name)) return fail(client, "execution request exceeds the payload limit");
  std::vector<uint8_t> payload = identity_payload(program_id);
  const size_t name_size = strlen(name);
  put_u32(payload, static_cast<uint32_t>(name_size));
  payload.insert(payload.end(), name, name + name_size);
  put_u64(payload, offset);
  put_u32(payload, size);
  const auto *input = static_cast<const uint8_t *>(bytes);
  if (size != 0) payload.insert(payload.end(), input, input + size);
  std::vector<uint8_t> response;
  return exchange(client, WriteProgramGlobal, payload, response);
}

extern "C" int gemstone_execution_bind_program_global(gemstone_execution_client *client, uint64_t program_id, const char *name,
                                                      uint64_t allocation_id, uint64_t offset) {
  if (client == nullptr || program_id == 0 || allocation_id == 0 || name == nullptr || name[0] == '\0' || strlen(name) > 4096) return -1;
  std::vector<uint8_t> payload = identity_payload(program_id);
  const size_t name_size = strlen(name);
  put_u32(payload, static_cast<uint32_t>(name_size));
  payload.insert(payload.end(), name, name + name_size);
  put_u64(payload, allocation_id);
  put_u64(payload, offset);
  std::vector<uint8_t> response;
  return exchange(client, BindProgramGlobal, payload, response);
}

extern "C" int gemstone_execution_launch(gemstone_execution_client *client, uint64_t program_id,
                                           const char *kernel_name, gemstone_dimensions group_size,
                                           gemstone_dimensions group_count, gemstone_dimensions global_size,
                                           uint32_t private_segment_bytes, uint32_t group_segment_bytes,
                                           const gemstone_kernel_argument *arguments, uint32_t argument_count,
                                           uint64_t *completion_id) {
  return launch_request(client, Launch, program_id, kernel_name, group_size, group_count, global_size,
                        3, gemstone_dimensions{0, 0, 0}, private_segment_bytes, group_segment_bytes,
                        arguments, argument_count, nullptr, 0, completion_id);
}

extern "C" int gemstone_execution_launch_with_geometry(
    gemstone_execution_client *client, uint64_t program_id, const char *kernel_name,
    gemstone_dimensions group_size, gemstone_dimensions group_count, gemstone_dimensions global_size,
    uint32_t work_dim, gemstone_dimensions global_offset,
    uint32_t private_segment_bytes, uint32_t group_segment_bytes,
    const gemstone_kernel_argument *arguments, uint32_t argument_count,
    uint64_t *completion_id) {
  return launch_request(client, LaunchWithGeometry, program_id, kernel_name, group_size, group_count, global_size,
                        work_dim, global_offset, private_segment_bytes, group_segment_bytes,
                        arguments, argument_count, nullptr, 0, completion_id);
}

extern "C" int gemstone_execution_launch_with_relocations(
    gemstone_execution_client *client, uint64_t program_id, const char *kernel_name,
    gemstone_dimensions group_size, gemstone_dimensions group_count, gemstone_dimensions global_size,
    uint32_t private_segment_bytes, uint32_t group_segment_bytes,
    const gemstone_kernel_argument *arguments, uint32_t argument_count,
    const gemstone_pointer_relocation *relocations, uint32_t relocation_count,
    uint64_t *completion_id) {
  return launch_request(client, LaunchWithRelocations, program_id, kernel_name, group_size, group_count, global_size,
                        3, gemstone_dimensions{0, 0, 0}, private_segment_bytes, group_segment_bytes,
                        arguments, argument_count, relocations, relocation_count, completion_id);
}

extern "C" int gemstone_execution_launch_with_geometry_and_relocations(
    gemstone_execution_client *client, uint64_t program_id, const char *kernel_name,
    gemstone_dimensions group_size, gemstone_dimensions group_count, gemstone_dimensions global_size,
    uint32_t work_dim, gemstone_dimensions global_offset,
    uint32_t private_segment_bytes, uint32_t group_segment_bytes,
    const gemstone_kernel_argument *arguments, uint32_t argument_count,
    const gemstone_pointer_relocation *relocations, uint32_t relocation_count,
    uint64_t *completion_id) {
  return launch_request(client, LaunchWithGeometryAndRelocations, program_id, kernel_name, group_size, group_count,
                        global_size, work_dim, global_offset, private_segment_bytes, group_segment_bytes,
                        arguments, argument_count, relocations, relocation_count, completion_id);
}

extern "C" int gemstone_execution_query_completion(gemstone_execution_client *client, uint64_t completion_id) {
  if (client == nullptr || completion_id == 0) return -1;
  std::vector<uint8_t> response;
  if (exchange(client, QueryCompletion, identity_payload(completion_id), response) != 0) return -1;
  if (response.size() != 4) return fail(client, "completion query returned an invalid status");
  const uint32_t status = get_u32(response.data());
  if (status == 1) return 1;
  if (status == 2) return 0;
  return fail(client, "completion failed");
}

extern "C" int gemstone_execution_completion_dirty_ranges(gemstone_execution_client *client, uint64_t completion_id,
                                                              gemstone_dirty_range *ranges, uint32_t capacity,
                                                              uint32_t *count) {
  if (client == nullptr || completion_id == 0 || count == nullptr || (capacity != 0 && ranges == nullptr)) return -1;
  std::vector<uint8_t> response;
  if (exchange(client, CompletionDirtyRanges, identity_payload(completion_id), response) != 0) return -1;
  if (response.size() < 4) return fail(client, "dirty-range response is truncated");
  const uint32_t required = get_u32(response.data());
  if (required > (kMaximumPayload - 4U) / 20U || response.size() != 4ULL + static_cast<uint64_t>(required) * 20ULL)
    return fail(client, "dirty-range response has an invalid size");
  *count = required;
  if (capacity == 0) return 0;
  if (capacity < required) return fail(client, "dirty-range capacity is insufficient");
  for (uint32_t index = 0; index < required; ++index) {
    const uint8_t *entry = response.data() + 4ULL + static_cast<uint64_t>(index) * 20ULL;
    ranges[index].allocation_id = get_u64(entry);
    ranges[index].offset = get_u64(entry + 8);
    ranges[index].size = get_u32(entry + 16);
    if (ranges[index].allocation_id == 0 || ranges[index].size == 0)
      return fail(client, "dirty-range response contains an invalid entry");
  }
  return 0;
}

extern "C" int gemstone_execution_completion_pointer_relocations(
    gemstone_execution_client *client, uint64_t completion_id,
    gemstone_pointer_relocation *relocations, uint32_t capacity, uint32_t *count) {
  if (client == nullptr || completion_id == 0 || count == nullptr ||
      (capacity != 0 && relocations == nullptr)) return -1;
  std::vector<uint8_t> response;
  if (exchange(client, CompletionPointerRelocations, identity_payload(completion_id), response) != 0) return -1;
  if (response.size() < 4) return fail(client, "pointer-relocation response is truncated");
  const uint32_t required = get_u32(response.data());
  if (required > (kMaximumPayload - 4U) / 32U ||
      response.size() != 4ULL + static_cast<uint64_t>(required) * 32ULL)
    return fail(client, "pointer-relocation response has an invalid size");
  *count = required;
  if (capacity == 0) return 0;
  if (capacity < required) return fail(client, "pointer-relocation capacity is insufficient");
  for (uint32_t index = 0; index < required; ++index) {
    const uint8_t *entry = response.data() + 4ULL + static_cast<uint64_t>(index) * 32ULL;
    relocations[index].source_allocation_id = get_u64(entry);
    relocations[index].source_argument_index = UINT32_MAX;
    relocations[index].source_offset = get_u64(entry + 8);
    relocations[index].target_allocation_id = get_u64(entry + 16);
    relocations[index].target_offset = get_u64(entry + 24);
    if (relocations[index].source_allocation_id == 0 || relocations[index].target_allocation_id == 0)
      return fail(client, "pointer-relocation response contains an invalid allocation");
  }
  return 0;
}

extern "C" int gemstone_execution_release_completion(gemstone_execution_client *client, uint64_t completion_id) {
  if (client == nullptr || completion_id == 0) return -1;
  std::vector<uint8_t> response;
  return exchange(client, ReleaseCompletion, identity_payload(completion_id), response);
}
