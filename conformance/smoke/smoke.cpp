#include <cassert>

#include "gemsi_cuda.h"

int main() {
  int count = 0;
  assert(cudaGetDeviceCount(&count) == 0);
  assert(count == 1);
  assert(cudaGetDeviceCount(nullptr) == 1);
  assert(gemsiAddI32(20, 22) == 42);
  return 0;
}
