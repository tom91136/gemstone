#include <cerrno>
#include <cstdarg>
#include <cstdio>
#include <cstring>
#include <optional>
#include <string_view>

#include <dlfcn.h>
#include <fcntl.h>
#include <unistd.h>

#include <gemstone/environment.hpp>
#include <gemstone/export.h>

#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/syscall.h>

namespace {

enum class Node { Module, InitState, Kfd };

constexpr std::string_view ModulePath = "/sys/module/amdgpu";
constexpr std::string_view InitStatePath = "/sys/module/amdgpu/initstate";
constexpr std::string_view KfdPath = "/dev/kfd";
constexpr std::string_view InitStateText = "live\n";

std::optional<Node> presented(const char *path) {
  if (!path) return std::nullopt;
  const std::string_view view(path);
  std::optional<Node> node;
  if (view == ModulePath) node = Node::Module;
  else if (view == InitStatePath) node = Node::InitState;
  else if (view == KfdPath) node = Node::Kfd;
  if (!node) return std::nullopt;
  const auto target = gemstone::environment::targetArch();
  if (!target || !target->starts_with("gfx")) return std::nullopt;
  return node;
}

template <typename F> F next(const char *name) { return reinterpret_cast<F>(dlsym(RTLD_NEXT, name)); }

int initStateDescriptor(int flags) {
  if ((flags & O_ACCMODE) != O_RDONLY) {
    errno = EACCES;
    return -1;
  }
  const int descriptor = memfd_create("initstate", (flags & O_CLOEXEC) != 0 ? MFD_CLOEXEC : 0U);
  if (descriptor < 0) return -1;
  if (static_cast<size_t>(write(descriptor, InitStateText.data(), InitStateText.size())) != InitStateText.size()
      || lseek(descriptor, 0, SEEK_SET) != 0) {
    close(descriptor);
    errno = EIO;
    return -1;
  }
  return descriptor;
}

mode_t modeOf(Node node) {
  if (node == Node::Module) return S_IFDIR | 0755;
  return node == Node::Kfd ? S_IFCHR | 0666 : S_IFREG | 0444;
}

template <typename S> void describe(S &status, Node node) {
  std::memset(&status, 0, sizeof(status));
  status.st_mode = modeOf(node);
  status.st_nlink = node == Node::Module ? 2 : 1;
  status.st_size = node != Node::InitState ? 0 : static_cast<decltype(status.st_size)>(InitStateText.size());
  status.st_blksize = 4096;
}

// glibc before 2.33 has no exported fstatat, and stat and fstatat64 share the kernel layout on x86_64
int passThrough(int directory, const char *path, void *status, int flags) {
  static const auto real = next<int (*)(int, const char *, void *, int)>("fstatat");
  return real ? real(directory, path, status, flags) : static_cast<int>(syscall(SYS_newfstatat, directory, path, status, flags));
}

template <typename S> bool describePresented(const char *path, S *status) {
  const auto node = presented(path);
  if (node) describe(*status, *node);
  return node.has_value();
}

int accessible(Node node, int mode) {
  if (((mode & W_OK) != 0 && node != Node::Kfd) || ((mode & X_OK) != 0 && node != Node::Module)) {
    errno = EACCES;
    return -1;
  }
  return 0;
}

mode_t creationMode(int flags, va_list &arguments) {
  const bool creating = (flags & O_CREAT) != 0 || (flags & O_TMPFILE) == O_TMPFILE;
  return creating ? va_arg(arguments, mode_t) : 0;
}

} // namespace

extern "C" {

GEMSTONE_PUBLIC_API int open(const char *path, int flags, ...) {
  va_list arguments;
  va_start(arguments, flags);
  const mode_t mode = creationMode(flags, arguments);
  va_end(arguments);
  if (presented(path) == Node::InitState) return initStateDescriptor(flags);
  static const auto real = next<int (*)(const char *, int, ...)>("open");
  return real(path, flags, mode);
}

GEMSTONE_PUBLIC_API int open64(const char *path, int flags, ...) {
  va_list arguments;
  va_start(arguments, flags);
  const mode_t mode = creationMode(flags, arguments);
  va_end(arguments);
  if (presented(path) == Node::InitState) return initStateDescriptor(flags);
  static const auto real = next<int (*)(const char *, int, ...)>("open64");
  return real(path, flags, mode);
}

GEMSTONE_PUBLIC_API int openat(int directory, const char *path, int flags, ...) {
  va_list arguments;
  va_start(arguments, flags);
  const mode_t mode = creationMode(flags, arguments);
  va_end(arguments);
  if (presented(path) == Node::InitState) return initStateDescriptor(flags);
  static const auto real = next<int (*)(int, const char *, int, ...)>("openat");
  return real(directory, path, flags, mode);
}

GEMSTONE_PUBLIC_API int openat64(int directory, const char *path, int flags, ...) {
  va_list arguments;
  va_start(arguments, flags);
  const mode_t mode = creationMode(flags, arguments);
  va_end(arguments);
  if (presented(path) == Node::InitState) return initStateDescriptor(flags);
  static const auto real = next<int (*)(int, const char *, int, ...)>("openat64");
  return real(directory, path, flags, mode);
}

// the sysroot's glibc defines these as inline wrappers, so the labels pin the exported names
GEMSTONE_PUBLIC_API int gemstone_stat(const char *path, struct stat *status) noexcept __asm__("stat");
GEMSTONE_PUBLIC_API int gemstone_stat64(const char *path, struct stat64 *status) noexcept __asm__("stat64");
GEMSTONE_PUBLIC_API int gemstone_lstat(const char *path, struct stat *status) noexcept __asm__("lstat");
GEMSTONE_PUBLIC_API int gemstone_lstat64(const char *path, struct stat64 *status) noexcept __asm__("lstat64");
GEMSTONE_PUBLIC_API int gemstone_fstatat(int directory, const char *path, struct stat *status, int flags) noexcept __asm__("fstatat");
GEMSTONE_PUBLIC_API int gemstone_fstatat64(int directory, const char *path, struct stat64 *status, int flags) noexcept __asm__("fstatat64");

GEMSTONE_PUBLIC_API int gemstone_stat(const char *path, struct stat *status) noexcept {
  return describePresented(path, status) ? 0 : passThrough(AT_FDCWD, path, status, 0);
}

GEMSTONE_PUBLIC_API int gemstone_stat64(const char *path, struct stat64 *status) noexcept {
  return describePresented(path, status) ? 0 : passThrough(AT_FDCWD, path, status, 0);
}

GEMSTONE_PUBLIC_API int gemstone_lstat(const char *path, struct stat *status) noexcept {
  return describePresented(path, status) ? 0 : passThrough(AT_FDCWD, path, status, AT_SYMLINK_NOFOLLOW);
}

GEMSTONE_PUBLIC_API int gemstone_lstat64(const char *path, struct stat64 *status) noexcept {
  return describePresented(path, status) ? 0 : passThrough(AT_FDCWD, path, status, AT_SYMLINK_NOFOLLOW);
}

GEMSTONE_PUBLIC_API int gemstone_fstatat(int directory, const char *path, struct stat *status, int flags) noexcept {
  return describePresented(path, status) ? 0 : passThrough(directory, path, status, flags);
}

GEMSTONE_PUBLIC_API int gemstone_fstatat64(int directory, const char *path, struct stat64 *status, int flags) noexcept {
  return describePresented(path, status) ? 0 : passThrough(directory, path, status, flags);
}

GEMSTONE_PUBLIC_API int access(const char *path, int mode) noexcept {
  if (const auto node = presented(path)) return accessible(*node, mode);
  static const auto real = next<int (*)(const char *, int)>("access");
  return real(path, mode);
}

GEMSTONE_PUBLIC_API int faccessat(int directory, const char *path, int mode, int flags) noexcept {
  if (const auto node = presented(path)) return accessible(*node, mode);
  static const auto real = next<int (*)(int, const char *, int, int)>("faccessat");
  return real(directory, path, mode, flags);
}

} // extern "C"
