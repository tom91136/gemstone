#include <atomic>
#include <cassert>
#include <thread>
#include <vector>

#include "gemstone_cuda.h"

int main() {
  std::atomic<bool> calls_ok = true;
  std::vector<std::thread> threads;
  for (int thread = 0; thread < 8; ++thread) {
    threads.emplace_back([thread, &calls_ok]() {
      for (int iteration = 0; iteration < 1000; ++iteration) {
        int count = 0;
        if (cudaGetDeviceCount(&count) != 0 || count != 1 || gemstoneAddI32(thread, iteration) != thread + iteration) {
          calls_ok = false;
        }
      }
    });
  }
  for (auto &thread : threads) {
    thread.join();
  }
  assert(calls_ok);
  return 0;
}
