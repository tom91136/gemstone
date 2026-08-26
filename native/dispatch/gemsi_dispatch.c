#define _GNU_SOURCE

#include "gemsi_dispatch.h"

#include <errno.h>
#include <pthread.h>
#include <stdatomic.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct GemsiCall {
  GemsiWireRequest request;
  GemsiWireResponse response;
  pthread_mutex_t mutex;
  pthread_cond_t changed;
  int complete;
  struct GemsiCall *next;
} GemsiCall;

typedef GemsiWireRequest *(*GemsiTakeRequest)(void);
typedef void (*GemsiCompleteRequest)(GemsiWireResponse *);

extern int32_t gemsiStartServer(GemsiTakeRequest take_request, GemsiCompleteRequest complete_request);

static pthread_once_t start_once = PTHREAD_ONCE_INIT;
static pthread_mutex_t state_mutex = PTHREAD_MUTEX_INITIALIZER;
static int server_state = 0;

static pthread_mutex_t queue_mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t queue_changed = PTHREAD_COND_INITIALIZER;
static GemsiCall *queue_head = NULL;
static GemsiCall *queue_tail = NULL;
static GemsiCall *active_call = NULL;

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

static void record_request(const GemsiWireRequest *request) {
  if (record_file == NULL) return;
  pthread_mutex_lock(&record_mutex);
  fprintf(
      record_file,
      "{\"phase\":\"request\",\"id\":%llu,\"thread\":%llu,\"opcode\":%u,\"operation\":\"%s\",\"argument0\":%lld,\"argument1\":%lld}\n",
      (unsigned long long)request->request_id,
      (unsigned long long)request->foreign_thread_id,
      request->opcode,
      gemsi_operation_name(request->opcode),
      (long long)request->argument0,
      (long long)request->argument1);
  fflush(record_file);
  pthread_mutex_unlock(&record_mutex);
}

static void record_response(const GemsiWireResponse *response) {
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

void gemsi_dispatch_record_boundary(uint32_t opcode, int32_t status) {
  if (record_file == NULL) return;
  pthread_mutex_lock(&record_mutex);
  fprintf(
      record_file,
      "{\"phase\":\"boundary\",\"thread\":%llu,\"opcode\":%u,\"operation\":\"%s\",\"status\":%d}\n",
      (unsigned long long)current_thread_id(),
      opcode,
      gemsi_operation_name(opcode),
      status);
  fflush(record_file);
  pthread_mutex_unlock(&record_mutex);
}

static GemsiWireRequest *take_request(void) {
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

static void complete_request(GemsiWireResponse *response) {
  GemsiCall *call = active_call;
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
  const char *record_path = getenv("GEMSI_RECORD");
  if (record_path != NULL && record_path[0] != '\0') {
    record_file = fopen(record_path, "a");
    if (record_file == NULL) {
      fprintf(stderr, "gemsi: cannot open recorder output %s: %s\n", record_path, strerror(errno));
    }
  }

  pthread_mutex_lock(&state_mutex);
  server_state = gemsiStartServer(take_request, complete_request) == GEMSI_STATUS_SUCCESS ? 1 : -1;
  pthread_mutex_unlock(&state_mutex);
}

__attribute__((constructor)) static void initialize_dispatch(void) {
  pthread_once(&start_once, start_server);
}

int gemsi_dispatch_request(GemsiWireRequest *request, GemsiWireResponse *response) {
  if (request == NULL || response == NULL) return -1;
  pthread_once(&start_once, start_server);

  pthread_mutex_lock(&state_mutex);
  const int ready = server_state == 1;
  pthread_mutex_unlock(&state_mutex);
  if (!ready) return -1;

  GemsiCall call;
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
