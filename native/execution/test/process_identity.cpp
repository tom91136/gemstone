// SPDX-License-Identifier: Apache-2.0
#include <cassert>
#include <cstdio>
#include <sys/wait.h>
#include <unistd.h>
#include "../gemstone_process_identity.h"
static unsigned long long fixture_start = 0;
static FILE *fixture_stat(const char *, const char *) {
  if (fixture_start == 0) return nullptr;
  FILE *file = tmpfile();
  assert(file != nullptr);
  std::fprintf(file, "%ld (command with spaces) S", static_cast<long>(getpid()));
  for (int field = 4; field < 22; ++field) std::fprintf(file, " 0");
  std::fprintf(file, " %llu 0\n", fixture_start);
  rewind(file);
  return file;
}
#undef GEMSTONE_PROCESS_IDENTITY_H
#define fopen fixture_stat
#define gemstone_process_incarnation fixture_incarnation
#include "../gemstone_process_identity.h"
#undef gemstone_process_incarnation
#undef fopen
int main() {
  assert(fixture_incarnation() == 0);
#ifndef __linux__
  assert(gemstone_process_incarnation() == 0);
  return 0;
#endif
  // With the PID fixed, different process-start values distinguish PID reuse.
  fixture_start = 12345;
  assert(fixture_incarnation() == 12345);
  fixture_start = 67890;
  assert(fixture_incarnation() == 67890);
  fixture_start = 0;
  const auto start = gemstone_process_incarnation();
  assert(start != 0 && start == gemstone_process_incarnation());
  const auto parent = getpid();
  const auto child = fork();
  assert(child >= 0);
  if (child == 0) {
    assert(getpid() != parent);
    assert(gemstone_process_incarnation() != 0);
    _exit(0);
  }
  int status = 0;
  assert(waitpid(child, &status, 0) == child);
  assert(WIFEXITED(status) && WEXITSTATUS(status) == 0);
}
