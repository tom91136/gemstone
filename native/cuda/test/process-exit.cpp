#include <cstdlib>

#include <cuda.h>

namespace {
void unload() {
  // Module unload waits for every stream, even when the handle is invalid.
  // CUDA's runtime registers its exit callback before the first driver work.
  if (cuModuleUnload(nullptr) != CUDA_ERROR_INVALID_VALUE) std::_Exit(EXIT_FAILURE);
}
} // namespace

int main() {
  if (std::atexit(unload) != 0) return EXIT_FAILURE;
  CUstream stream = nullptr;
  if (cuStreamCreate(&stream, CU_STREAM_DEFAULT) != CUDA_SUCCESS) return EXIT_FAILURE;
  return EXIT_SUCCESS;
}
