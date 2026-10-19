#pragma once

#include <utility>

#include <unistd.h>

namespace gemstone {

/** Owns a file descriptor and closes it on destruction. */
class FileDescriptor {
public:
  explicit FileDescriptor(int value = -1) noexcept : descriptor(value) {}
  ~FileDescriptor() {
    if (descriptor >= 0) close(descriptor);
  }

  FileDescriptor(const FileDescriptor &) = delete;
  FileDescriptor &operator=(const FileDescriptor &) = delete;

  FileDescriptor(FileDescriptor &&other) noexcept : descriptor(std::exchange(other.descriptor, -1)) {}
  FileDescriptor &operator=(FileDescriptor &&other) noexcept {
    if (this != &other) {
      if (descriptor >= 0) close(descriptor);
      descriptor = std::exchange(other.descriptor, -1);
    }
    return *this;
  }

  [[nodiscard]] int get() const noexcept { return descriptor; }
  [[nodiscard]] int release() noexcept { return std::exchange(descriptor, -1); }

private:
  int descriptor;
};

} // namespace gemstone
