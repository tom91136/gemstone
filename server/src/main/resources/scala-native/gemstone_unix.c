#define _GNU_SOURCE
#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <stddef.h>
#include <stdint.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#include <sys/socket.h>
#include <sys/un.h>

/* All descriptors are nonblocking and close-on-exec. Scala duplicates under a
 * lock before blocking, so concurrent close cannot reuse an in-flight fd. */
static int configured(int fd) {
  if (fd < 0) return -errno;
  if (fcntl(fd, F_SETFD, FD_CLOEXEC) < 0 || fcntl(fd, F_SETFL, O_NONBLOCK) < 0) {
    int error = errno;
    close(fd);
    return -error;
  }
  return fd;
}
static int address(const char *path, struct sockaddr_un *out) {
  size_t length = strlen(path);
  if (length == 0 || length >= sizeof(out->sun_path)) return -ENAMETOOLONG;
  memset(out, 0, sizeof(*out));
  out->sun_family = AF_UNIX;
  memcpy(out->sun_path, path, length + 1);
  return 0;
}
static int64_t monotonic_millis(void) {
  struct timespec time;
  clock_gettime(CLOCK_MONOTONIC, &time);
  return (int64_t)time.tv_sec * 1000 + time.tv_nsec / 1000000;
}
static int ready(int fd, short events, int timeout) {
  struct pollfd p = {fd, events, 0};
  const int64_t deadline = monotonic_millis() + timeout;
  for (;;) {
    int64_t remaining = timeout == 0 ? -1 : deadline - monotonic_millis();
    if (timeout != 0 && remaining <= 0) return -ETIMEDOUT;
    int result = poll(&p, 1, (int)remaining);
    if (result < 0 && errno == EINTR) continue;
    if (result == 0) return -ETIMEDOUT;
    return result < 0 ? -errno : 0;
  }
}

int gemstone_unix_open(const char *path, int listen_socket, int timeout) {
  struct sockaddr_un addr;
  int result = address(path, &addr);
  if (result < 0) return result;
#ifdef __linux__
  int fd = configured(socket(AF_UNIX, SOCK_STREAM | SOCK_CLOEXEC | SOCK_NONBLOCK, 0));
#else
  int fd = configured(socket(AF_UNIX, SOCK_STREAM, 0));
#endif
  if (fd < 0) return fd;
  if (listen_socket) {
    if (bind(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0) result = -errno;
    else if (listen(fd, 128) < 0) {
      result = -errno;
      unlink(path);
    }
  } else if (connect(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    result = -errno;
    if (errno == EINPROGRESS) {
      result = ready(fd, POLLOUT, timeout);
      if (result == 0) {
        int error = 0;
        socklen_t size = sizeof(error);
        result = getsockopt(fd, SOL_SOCKET, SO_ERROR, &error, &size) < 0 ? -errno : -error;
      }
    }
  }
  if (result < 0) {
    close(fd);
    return result;
  }
  return fd;
}
int gemstone_unix_dup(int fd) {
  int result = fcntl(fd, F_DUPFD_CLOEXEC, 0);
  return result < 0 ? -errno : result;
}
void gemstone_unix_close(int fd) { close(fd); }
void gemstone_unix_shutdown(int fd) {
  shutdown(fd, SHUT_RDWR);
  close(fd);
}
int gemstone_unix_accept(int fd) {
  int result = ready(fd, POLLIN, 100);
  if (result < 0) return result;
#ifdef __linux__
  result = configured(accept4(fd, NULL, NULL, SOCK_CLOEXEC | SOCK_NONBLOCK));
#else
  result = configured(accept(fd, NULL, NULL));
#endif
  return result == -EAGAIN || result == -EWOULDBLOCK ? -ETIMEDOUT : result;
}
int gemstone_unix_read(int fd, int8_t *bytes, int size, int timeout) {
  for (;;) {
    ssize_t count = recv(fd, bytes, (size_t)size, 0);
    if (count >= 0) return (int)count;
    if (errno == EINTR) continue;
    if (errno != EAGAIN && errno != EWOULDBLOCK) return -errno;
    int result = ready(fd, POLLIN, timeout);
    if (result < 0) return result;
  }
}
int gemstone_unix_write(int fd, const int8_t *bytes, int size, int timeout) {
  for (;;) {
    ssize_t count = send(fd, bytes, (size_t)size, MSG_NOSIGNAL);
    if (count >= 0) return (int)count;
    if (errno == EINTR) continue;
    if (errno != EAGAIN && errno != EWOULDBLOCK) return -errno;
    int result = ready(fd, POLLOUT, timeout);
    if (result < 0) return result;
  }
}
int gemstone_unix_timeout(int result) { return result == -ETIMEDOUT; }
const char *gemstone_unix_error(int result) { return strerror(-result); }
