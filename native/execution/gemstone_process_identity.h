#ifndef GEMSTONE_PROCESS_IDENTITY_H
#define GEMSTONE_PROCESS_IDENTITY_H
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/* PID plus Linux process start time is stable across DSOs and changes after PID reuse. */
static inline uint64_t gemstone_process_incarnation(void) {
#ifdef __linux__
  FILE *file = fopen("/proc/self/stat", "r");
  if (file != NULL) {
    char text[4096];
    const int present = fgets(text, sizeof(text), file) != NULL;
    fclose(file);
    char *cursor = present ? strrchr(text, ')') : NULL;
    if (cursor != NULL) {
      cursor += 2;
      for (int field = 3; field < 22; ++field) {
        cursor = strchr(cursor, ' ');
        if (cursor == NULL) break;
        ++cursor;
      }
      if (cursor != NULL) {
        const uint64_t value = strtoull(cursor, NULL, 10);
        if (value != 0) return value;
      }
    }
  }
#endif
  /* No stable cross-DSO process-start identity: callers must fail closed. */
  return 0;
}
#endif
