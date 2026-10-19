#include <cstdlib>

#include <hip/hip_runtime_api.h>

extern "C" void **__hipRegisterFatBinary(const void *);
extern "C" void __hipUnregisterFatBinary(void **);

namespace {
void **module;
void unregisterModule() { __hipUnregisterFatBinary(module); }
// Match a compiler-generated translation unit: register the module and its
// exit callback before main first creates the command queue. No kernel image
// is loaded in this regression; registration is deliberately lazy.
struct Registration {
  Registration() {
    module = __hipRegisterFatBinary(nullptr);
    std::atexit(unregisterModule);
  }
} registration;
} // namespace

int main() {
  hipStream_t first{};
  hipStream_t second{};
  if (hipStreamCreateWithFlags(&first, hipStreamNonBlocking) != hipSuccess) return 1;
  if (hipStreamCreateWithFlags(&second, hipStreamNonBlocking) != hipSuccess) return 2;
  return 0; // The exit callback must still have a live queue to drain.
}
