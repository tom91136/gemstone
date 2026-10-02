#define _GNU_SOURCE
#include <dlfcn.h>
#include <stddef.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>

#include <sys/syscall.h>
#include <sys/uio.h>
static size_t hx(char *b, uintptr_t x) {
  const char *d = "0123456789abcdef";
  char t[2 * sizeof(x)];
  size_t n = 0;
  do {
    t[n++] = d[x & 15];
    x >>= 4;
  } while (x);
  for (size_t i = 0; i < n; i++)
    b[i] = t[n - i - 1];
  return n;
}
static size_t dec(char *b, size_t x) {
  char t[32];
  size_t n = 0;
  do {
    t[n++] = (char)('0' + x % 10);
    x /= 10;
  } while (x);
  for (size_t i = 0; i < n; i++)
    b[i] = t[n - i - 1];
  return n;
}
static void writev_all(int fd, struct iovec *v, int count) {
  while (count) {
    ssize_t n = syscall(SYS_writev, fd, v, count);
    if (n <= 0) return;
    while (count && (size_t)n >= v[0].iov_len) {
      n -= (ssize_t)v[0].iov_len;
      v++;
      count--;
    }
    if (count && n) {
      v[0].iov_base = (char *)v[0].iov_base + n;
      v[0].iov_len -= (size_t)n;
    }
  }
}
void *memcpy(void *d, const void *s, size_t n) {
  void *(*real_memcpy)(void *, const void *, size_t) = dlsym(RTLD_NEXT, "memcpy");
  uintptr_t pc = (uintptr_t)__builtin_extract_return_addr(__builtin_return_address(0));
  if (pc == 0x455203) {
    char b[128];
    size_t k = 0;
    b[k++] = '\n';
    b[k++] = '<';
    b[k++] = 'P';
    b[k++] = ' ';
    b[k++] = '0';
    b[k++] = 'x';
    k += hx(b + k, pc);
    b[k++] = ' ';
    b[k++] = '0';
    b[k++] = 'x';
    k += hx(b + k, (uintptr_t)d);
    b[k++] = ' ';
    b[k++] = '0';
    b[k++] = 'x';
    k += hx(b + k, (uintptr_t)s);
    b[k++] = ' ';
    k += dec(b + k, n);
    b[k++] = '>';
    b[k++] = '\n';
    struct iovec v[2] = {{b, k}, {(void *)s, n}};
    writev_all(STDOUT_FILENO, v, n ? 2 : 1);
  }
  return real_memcpy(d, s, n);
}
