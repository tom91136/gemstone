#define _GNU_SOURCE

#include "gemstone_dispatch.h"

#include <errno.h>
#include <pthread.h>
#include <stdatomic.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct GemstoneCall {
  GemstoneWireRequest request;
  GemstoneWireResponse response;
  pthread_mutex_t mutex;
  pthread_cond_t changed;
  int complete;
  struct GemstoneCall *next;
} GemstoneCall;

typedef GemstoneWireRequest *(*GemstoneTakeRequest)(void);
typedef void (*GemstoneCompleteRequest)(GemstoneWireResponse *);

extern int32_t gemstoneStartServer(GemstoneTakeRequest take_request, GemstoneCompleteRequest complete_request);

static pthread_once_t start_once = PTHREAD_ONCE_INIT;
static pthread_mutex_t state_mutex = PTHREAD_MUTEX_INITIALIZER;
static int server_state = 0;

static pthread_mutex_t queue_mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t queue_changed = PTHREAD_COND_INITIALIZER;
static GemstoneCall *queue_head = NULL;
static GemstoneCall *queue_tail = NULL;
static GemstoneCall *active_call = NULL;

static _Atomic uint64_t next_request_id = 1;
static pthread_mutex_t record_mutex = PTHREAD_MUTEX_INITIALIZER;
static FILE *record_file = NULL;

static uint64_t current_thread_id(void) {
  const pthread_t self = pthread_self();
  const unsigned char *bytes = (const unsigned char *)&self;
  uint64_t hash = UINT64_C(1469598103934665603);
  for (size_t index = 0; index < sizeof(self); ++index) {
    hash ^= bytes[index];
    hash *= UINT64_C(1099511628211);
  }
  return hash;
}

static void record_request(const GemstoneWireRequest *request) {
  if (record_file == NULL) return;
  pthread_mutex_lock(&record_mutex);
  fprintf(
      record_file,
      "{\"phase\":\"request\",\"id\":%llu,\"thread\":%llu,\"opcode\":%u,\"operation\":\"%s\",\"argument0\":%lld,\"argument1\":%lld}\n",
      (unsigned long long)request->request_id,
      (unsigned long long)request->foreign_thread_id,
      request->opcode,
      gemstone_operation_name(request->opcode),
      (long long)request->argument0,
      (long long)request->argument1);
  fflush(record_file);
  pthread_mutex_unlock(&record_mutex);
}

static void record_response(const GemstoneWireResponse *response) {
  if (record_file == NULL) return;
  pthread_mutex_lock(&record_mutex);
  fprintf(
      record_file,
      "{\"phase\":\"response\",\"id\":%llu,\"status\":%d,\"result0\":%lld}\n",
      (unsigned long long)response->request_id,
      response->status,
      (long long)response->result0);
  fflush(record_file);
  pthread_mutex_unlock(&record_mutex);
}

void gemstone_dispatch_record_boundary(uint32_t opcode, int32_t status) {
  if (record_file == NULL) return;
  pthread_mutex_lock(&record_mutex);
  fprintf(
      record_file,
      "{\"phase\":\"boundary\",\"thread\":%llu,\"opcode\":%u,\"operation\":\"%s\",\"status\":%d}\n",
      (unsigned long long)current_thread_id(),
      opcode,
      gemstone_operation_name(opcode),
      status);
  fflush(record_file);
  pthread_mutex_unlock(&record_mutex);
}

static GemstoneWireRequest *take_request(void) {
  pthread_mutex_lock(&queue_mutex);
  while (queue_head == NULL) {
    pthread_cond_wait(&queue_changed, &queue_mutex);
  }
  active_call = queue_head;
  queue_head = active_call->next;
  if (queue_head == NULL) queue_tail = NULL;
  active_call->next = NULL;
  record_request(&active_call->request);
  pthread_mutex_unlock(&queue_mutex);
  return &active_call->request;
}

static void complete_request(GemstoneWireResponse *response) {
  GemstoneCall *call = active_call;
  if (call == NULL) abort();
  record_response(response);
  pthread_mutex_lock(&call->mutex);
  call->response = *response;
  call->complete = 1;
  pthread_cond_signal(&call->changed);
  pthread_mutex_unlock(&call->mutex);
  active_call = NULL;
}

static void start_server(void) {
  const char *record_path = getenv("GEMSTONE_RECORD");
  if (record_path != NULL && record_path[0] != '\0') {
    record_file = fopen(record_path, "a");
    if (record_file == NULL) {
      fprintf(stderr, "gemstone: cannot open recorder output %s: %s\n", record_path, strerror(errno));
    }
  }

  pthread_mutex_lock(&state_mutex);
  server_state = gemstoneStartServer(take_request, complete_request) == GEMSTONE_STATUS_SUCCESS ? 1 : -1;
  pthread_mutex_unlock(&state_mutex);
}

__attribute__((constructor)) static void initialize_dispatch(void) {
  pthread_once(&start_once, start_server);
}

int gemstone_dispatch_request(GemstoneWireRequest *request, GemstoneWireResponse *response) {
  if (request == NULL || response == NULL) return -1;
  pthread_once(&start_once, start_server);

  pthread_mutex_lock(&state_mutex);
  const int ready = server_state == 1;
  pthread_mutex_unlock(&state_mutex);
  if (!ready) return -1;

  GemstoneCall call;
  memset(&call, 0, sizeof(call));
  call.request = *request;
  call.request.request_id = atomic_fetch_add(&next_request_id, 1);
  call.request.foreign_thread_id = current_thread_id();
  if (pthread_mutex_init(&call.mutex, NULL) != 0) return -1;
  if (pthread_cond_init(&call.changed, NULL) != 0) {
    pthread_mutex_destroy(&call.mutex);
    return -1;
  }

  pthread_mutex_lock(&call.mutex);
  pthread_mutex_lock(&queue_mutex);
  if (queue_tail == NULL) queue_head = &call;
  else queue_tail->next = &call;
  queue_tail = &call;
  pthread_cond_signal(&queue_changed);
  pthread_mutex_unlock(&queue_mutex);

  while (!call.complete) {
    pthread_cond_wait(&call.changed, &call.mutex);
  }
  *request = call.request;
  *response = call.response;
  pthread_mutex_unlock(&call.mutex);
  pthread_cond_destroy(&call.changed);
  pthread_mutex_destroy(&call.mutex);
  return 0;
}
