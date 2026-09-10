#include <stdint.h>
#include <stdio.h>

extern int32_t cuInit(uint32_t flags);

int main(void) {
  const int32_t status = cuInit(0);
  if (status != 1) {
    fprintf(stderr, "expected authentication failure status 1, got %d\n", status);
    return 1;
  }
  return 0;
}
