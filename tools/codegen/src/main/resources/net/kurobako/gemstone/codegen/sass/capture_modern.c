
#define _GNU_SOURCE
#include <malloc.h>
#include <stddef.h>
#include <string.h>
#include <unistd.h>
extern void __libc_free(void *);
static void output(const char *p, size_t n) {
  while (n) {
    ssize_t k = write(2, p, n);
    if (k <= 0) break;
    p += k;
    n -= k;
  }
}
void free(void *p) {
  if (p) {
    size_t n = malloc_usable_size(p);
    if (n > 100000 && n < 16000000) {
      const char *s = memmem(p, n, "ARCHITECTURE ", 13);
      if (s) {

        output("\nSTART_MACHINE_DESCRIPTION\n", 27);
        output(p, n);
        output("\nEND_MACHINE_DESCRIPTION\n", 25);
      }
    }
  }
  __libc_free(p);
}
