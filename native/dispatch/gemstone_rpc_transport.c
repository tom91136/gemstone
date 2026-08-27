#define _POSIX_C_SOURCE 200112L

#include "gemstone_rpc_transport.h"

#include <errno.h>
#include <netdb.h>
#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "gemstone_cuda_rpc.inc"
#include <sys/socket.h>
#include <sys/time.h>

enum {
  GEMSTONE_RPC_PROTOCOL_VERSION = 2,
  GEMSTONE_RPC_REQUEST_HEADER_SIZE = 160,
  GEMSTONE_RPC_RESPONSE_HEADER_SIZE = 20,
  GEMSTONE_RPC_CALLBACK_REQUEST_REMAINDER_SIZE = 20,
  GEMSTONE_RPC_FINGERPRINT_SIZE = 64,
  GEMSTONE_RPC_TOKEN_SIZE = 64,
  GEMSTONE_RPC_MAXIMUM_NAME_SIZE = 128
};

static const uint32_t request_magic = UINT32_C(0x474d5331);
static const uint32_t response_magic = UINT32_C(0x474d5231);
static const uint32_t callback_magic = UINT32_C(0x474d4331);
static const uint32_t callback_reply_magic = UINT32_C(0x474d4131);
static pthread_mutex_t connection_mutex = PTHREAD_MUTEX_INITIALIZER;
static int connection_fd = -1;

enum { GEMSTONE_CALLBACK_HOST = 1, GEMSTONE_CALLBACK_STREAM = 2 };

typedef struct GemstoneCallbackEntry {
  uint64_t id;
  uint32_t kind;
  GemstoneHostCallback host;
  GemstoneStreamCallback stream;
  void *user_data;
  struct GemstoneCallbackEntry *next;
} GemstoneCallbackEntry;

static pthread_mutex_t callback_mutex = PTHREAD_MUTEX_INITIALIZER;
static GemstoneCallbackEntry *callbacks = NULL;
static uint64_t next_callback_id = UINT64_C(1);

static void record_call(const char *function_name, uint64_t message_id_high, uint64_t message_id_low, uint32_t request_size, int32_t status,
                        uint32_t response_size) {
  const char *path = getenv("GEMSTONE_RECORD");
  if (path == NULL || path[0] == '\0') return;
  FILE *file = fopen(path, "a");
  if (file == NULL) return;
  fprintf(file,
          "{\"transport\":\"jvm-rpc\",\"function\":\"%s\",\"messageId\":\"%016llx%016llx\",\"requestBytes\":%u,\"status\":%d,"
          "\"responseBytes\":%u}\n",
          function_name, (unsigned long long)message_id_high, (unsigned long long)message_id_low, request_size, status, response_size);
  fclose(file);
}

static void put_u32(uint8_t *destination, uint32_t value) {
  destination[0] = (uint8_t)(value >> 24);
  destination[1] = (uint8_t)(value >> 16);
  destination[2] = (uint8_t)(value >> 8);
  destination[3] = (uint8_t)value;
}

static void put_u64(uint8_t *destination, uint64_t value) {
  put_u32(destination, (uint32_t)(value >> 32));
  put_u32(destination + 4, (uint32_t)value);
}

static uint32_t get_u32(const uint8_t *source) {
  return ((uint32_t)source[0] << 24) | ((uint32_t)source[1] << 16) | ((uint32_t)source[2] << 8) | (uint32_t)source[3];
}

static uint64_t get_u64(const uint8_t *source) {
  return ((uint64_t)get_u32(source) << 32) | get_u32(source + 4);
}

static uint64_t register_callback(uint32_t kind, GemstoneHostCallback host, GemstoneStreamCallback stream, void *user_data) {
  GemstoneCallbackEntry *entry = (GemstoneCallbackEntry *)calloc(1, sizeof(*entry));
  if (entry == NULL) return 0;
  entry->kind = kind;
  entry->host = host;
  entry->stream = stream;
  entry->user_data = user_data;
  pthread_mutex_lock(&callback_mutex);
  if (next_callback_id == 0) next_callback_id = 1;
  entry->id = next_callback_id++;
  entry->next = callbacks;
  callbacks = entry;
  pthread_mutex_unlock(&callback_mutex);
  return entry->id;
}

uint64_t gemstone_rpc_register_host_callback(GemstoneHostCallback callback, void *user_data) {
  if (callback == NULL) return 0;
  return register_callback(GEMSTONE_CALLBACK_HOST, callback, NULL, user_data);
}

uint64_t gemstone_rpc_register_stream_callback(GemstoneStreamCallback callback, void *user_data) {
  if (callback == NULL) return 0;
  return register_callback(GEMSTONE_CALLBACK_STREAM, NULL, callback, user_data);
}

void gemstone_rpc_unregister_callback(uint64_t callback_id) {
  pthread_mutex_lock(&callback_mutex);
  GemstoneCallbackEntry **cursor = &callbacks;
  while (*cursor != NULL && (*cursor)->id != callback_id) cursor = &(*cursor)->next;
  if (*cursor != NULL) {
    GemstoneCallbackEntry *entry = *cursor;
    *cursor = entry->next;
    free(entry);
  }
  pthread_mutex_unlock(&callback_mutex);
}

static GemstoneCallbackEntry *take_callback(uint64_t callback_id, uint32_t kind) {
  pthread_mutex_lock(&callback_mutex);
  GemstoneCallbackEntry **cursor = &callbacks;
  while (*cursor != NULL && ((*cursor)->id != callback_id || (*cursor)->kind != kind)) cursor = &(*cursor)->next;
  GemstoneCallbackEntry *entry = *cursor;
  if (entry != NULL) *cursor = entry->next;
  pthread_mutex_unlock(&callback_mutex);
  return entry;
}

static void clear_callbacks(void) {
  pthread_mutex_lock(&callback_mutex);
  while (callbacks != NULL) {
    GemstoneCallbackEntry *entry = callbacks;
    callbacks = entry->next;
    free(entry);
  }
  pthread_mutex_unlock(&callback_mutex);
}

static int send_all(int fd, const uint8_t *data, size_t size) {
  while (size != 0) {
    const ssize_t sent = send(fd, data, size, MSG_NOSIGNAL);
    if (sent < 0 && errno == EINTR) continue;
    if (sent <= 0) return -1;
    data += (size_t)sent;
    size -= (size_t)sent;
  }
  return 0;
}

static int receive_all(int fd, uint8_t *data, size_t size) {
  while (size != 0) {
    const ssize_t received = recv(fd, data, size, 0);
    if (received < 0 && errno == EINTR) continue;
    if (received <= 0) return -1;
    data += (size_t)received;
    size -= (size_t)received;
  }
  return 0;
}

static void close_connection(void) {
  if (connection_fd >= 0) close(connection_fd);
  connection_fd = -1;
  clear_callbacks();
}

static int service_callback(int fd) {
  uint8_t header[GEMSTONE_RPC_CALLBACK_REQUEST_REMAINDER_SIZE];
  if (receive_all(fd, header, sizeof(header)) != 0) return -1;
  const uint32_t version = get_u32(header);
  const uint64_t callback_id = get_u64(header + 4);
  const uint32_t kind = get_u32(header + 12);
  const uint32_t payload_size = get_u32(header + 16);
  if (version != GEMSTONE_RPC_PROTOCOL_VERSION || callback_id == 0 || payload_size > 64) return -1;

  uint8_t payload[64];
  if (payload_size != 0 && receive_all(fd, payload, payload_size) != 0) return -1;
  int32_t status = 1;
  GemstoneCallbackEntry *entry = take_callback(callback_id, kind);
  if (entry != NULL) {
    if (kind == GEMSTONE_CALLBACK_HOST && payload_size == 0 && entry->host != NULL) {
      entry->host(entry->user_data);
      status = 0;
    } else if (kind == GEMSTONE_CALLBACK_STREAM && payload_size == 12 && entry->stream != NULL) {
      entry->stream((void *)(uintptr_t)get_u64(payload), (int32_t)get_u32(payload + 8), entry->user_data);
      status = 0;
    }
    free(entry);
  }

  uint8_t reply[GEMSTONE_RPC_RESPONSE_HEADER_SIZE];
  put_u32(reply, callback_reply_magic);
  put_u32(reply + 4, GEMSTONE_RPC_PROTOCOL_VERSION);
  put_u32(reply + 8, (uint32_t)status);
  put_u32(reply + 12, 0);
  put_u32(reply + 16, 0);
  return send_all(fd, reply, sizeof(reply));
}

static int connect_to_server(void) {
  if (connection_fd >= 0) return 0;
  const char *port = getenv("GEMSTONE_RPC_PORT");
  if (port == NULL || port[0] == '\0') return -1;

  struct addrinfo hints;
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;
  struct addrinfo *addresses = NULL;
  if (getaddrinfo("127.0.0.1", port, &hints, &addresses) != 0) return -1;

  int fd = -1;
  for (const struct addrinfo *address = addresses; address != NULL; address = address->ai_next) {
    fd = socket(address->ai_family, address->ai_socktype, address->ai_protocol);
    if (fd < 0) continue;
    if (connect(fd, address->ai_addr, address->ai_addrlen) == 0) break;
    close(fd);
    fd = -1;
  }
  freeaddrinfo(addresses);
  if (fd < 0) return -1;
  const struct timeval timeout = {.tv_sec = 30, .tv_usec = 0};
  if (setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) != 0
      || setsockopt(fd, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) != 0) {
    close(fd);
    return -1;
  }
  connection_fd = fd;
  return 0;
}

void gemstone_rpc_buffer_release(GemstoneRpcBuffer *buffer) {
  if (buffer == NULL) return;
  free(buffer->data);
  buffer->data = NULL;
  buffer->size = 0;
}

int gemstone_rpc_call(uint64_t message_id_high, uint64_t message_id_low, const char *function_name, const uint8_t *payload,
                   uint32_t payload_size, int32_t *cuda_status, GemstoneRpcBuffer *response) {
  if (function_name == NULL || cuda_status == NULL || response == NULL) return -1;
  const size_t name_size = strlen(function_name);
  if (name_size == 0 || name_size > GEMSTONE_RPC_MAXIMUM_NAME_SIZE || payload_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE) return -1;
  if (payload_size != 0 && payload == NULL) return -1;
  const char *token = getenv("GEMSTONE_RPC_TOKEN");
  if (token == NULL || strlen(token) != GEMSTONE_RPC_TOKEN_SIZE) return -1;

  response->data = NULL;
  response->size = 0;
  uint8_t header[GEMSTONE_RPC_REQUEST_HEADER_SIZE];
  memset(header, 0, sizeof(header));
  put_u32(header, request_magic);
  put_u32(header + 4, GEMSTONE_RPC_PROTOCOL_VERSION);
  memcpy(header + 8, GEMSTONE_CUDA_RPC_SCHEMA_FINGERPRINT, GEMSTONE_RPC_FINGERPRINT_SIZE);
  memcpy(header + 72, token, GEMSTONE_RPC_TOKEN_SIZE);
  put_u64(header + 136, message_id_high);
  put_u64(header + 144, message_id_low);
  put_u32(header + 152, (uint32_t)name_size);
  put_u32(header + 156, payload_size);

  pthread_mutex_lock(&connection_mutex);
  int result = -1;
  if (connect_to_server() != 0 || send_all(connection_fd, header, sizeof(header)) != 0
      || send_all(connection_fd, (const uint8_t *)function_name, name_size) != 0
      || (payload_size != 0 && send_all(connection_fd, payload, payload_size) != 0)) {
    close_connection();
    goto complete;
  }

  uint8_t response_header[GEMSTONE_RPC_RESPONSE_HEADER_SIZE];
  for (;;) {
    if (receive_all(connection_fd, response_header, 4) != 0) {
      close_connection();
      goto complete;
    }
    const uint32_t magic = get_u32(response_header);
    if (magic == callback_magic) {
      if (service_callback(connection_fd) != 0) {
        close_connection();
        goto complete;
      }
      continue;
    }
    if (magic != response_magic || receive_all(connection_fd, response_header + 4, sizeof(response_header) - 4) != 0
        || get_u32(response_header + 4) != GEMSTONE_RPC_PROTOCOL_VERSION) {
      close_connection();
      goto complete;
    }
    break;
  }
  *cuda_status = (int32_t)get_u32(response_header + 8);
  const uint32_t message_size = get_u32(response_header + 12);
  const uint32_t response_size = get_u32(response_header + 16);
  if (message_size > 1024 || response_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE) {
    close_connection();
    goto complete;
  }

  char error_message[1025];
  if (message_size != 0) {
    if (receive_all(connection_fd, (uint8_t *)error_message, message_size) != 0) {
      close_connection();
      goto complete;
    }
    error_message[message_size] = '\0';
    if (getenv("GEMSTONE_RPC_DEBUG") != NULL) fprintf(stderr, "gemstone: %s\n", error_message);
  }
  if (response_size != 0) {
    response->data = (uint8_t *)malloc(response_size);
    if (response->data == NULL || receive_all(connection_fd, response->data, response_size) != 0) {
      gemstone_rpc_buffer_release(response);
      close_connection();
      goto complete;
    }
    response->size = response_size;
  }
  record_call(function_name, message_id_high, message_id_low, payload_size, *cuda_status, response_size);
  result = 0;

complete:
  pthread_mutex_unlock(&connection_mutex);
  return result;
}
