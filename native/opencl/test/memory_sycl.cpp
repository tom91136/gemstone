// Native memory transport and scheduling regression client.
#include <sycl/sycl.hpp>

#include "../../rpc/test/memory_patterns.hpp"

namespace memory_patterns {
struct MemoryClient {
  std::string kind;
  std::string allocationKind() const { return kind; }
  explicit MemoryClient(std::string kind, uint64_t = 0) : kind(std::move(kind)) {}
  sycl::queue queue{sycl::gpu_selector_v};
  std::vector<sycl::queue> overlapQueues;
  void overlapSubmit(unsigned, void *input, uint32_t *output, unsigned mode) {
    overlapQueues.emplace_back(queue.get_context(), queue.get_device());
    enqueue(overlapQueues.back(), input, output, reinterpret_cast<uint32_t *>(input), mode, 1);
  }
  void overlapFinish() {
    for (auto &q : overlapQueues)
      q.wait_and_throw();
  }

  void *allocateDevice(size_t size) {
    auto *p = sycl::malloc_device(size, queue);
    require(p, "SYCL device allocation");
    return p;
  }
  void releaseDevice(void *p) { sycl::free(p, queue); }
  void deviceWrite(void *p, const void *bytes, size_t size) { queue.memcpy(p, bytes, size).wait_and_throw(); }
  void deviceRead(void *bytes, const void *p, size_t size) { queue.memcpy(bytes, p, size).wait_and_throw(); }
  void *allocateBacking(size_t size) {
    auto *p = (allocationKind() == "host") ? sycl::aligned_alloc_host(64, size, queue) : sycl::aligned_alloc_shared(64, size, queue);
    require(p, "SYCL shared allocation");
    return p;
  }
  void releaseBacking(void *p) { sycl::free(p, queue); }
  template <class WideWord>
  void enqueueTyped(sycl::queue &target, void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    target.parallel_for(sycl::nd_range<1>(count, count == 1 ? 1 : 32), [=](sycl::nd_item<1> item) {
      const bool fenced = mode == 7 || mode == 8;
      if (mode >= 15 && mode <= 22) {
        auto *p = static_cast<uint32_t *>(input);
        using HostAtomic =
            sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>;
        using DeviceAtomic =
            sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::device, sycl::access::address_space::global_space>;
        if (mode == 15) {
          output[0] = 1;
          HostAtomic(p[0]).store(1);
          while (HostAtomic(p[0]).load() != 3) {
          }
        } else if (mode == 16) {
          output[0] = 2;
          HostAtomic(p[1]).store(2);
        } else if (mode == 17) {
          p[4] = p[2];
          HostAtomic(p[0]).store(1);
          while (DeviceAtomic(output[0]).load() != 1) {
          }
          p[3] = p[2];
          DeviceAtomic(output[1]).store(2);
        } else if (mode == 18) {
          p[2] = 73;
          DeviceAtomic(output[0]).store(1);
          while (DeviceAtomic(output[1]).load() != 2) {
          }
          HostAtomic(p[1]).store(2);
        } else if (mode == 21 || mode == 22) {
          auto *control = static_cast<Node *>(input);
          auto *table = reinterpret_cast<Node *>(output);
          if (mode == 21) {
            table->next = control->next;
            HostAtomic(control->value).store(1);
            while (DeviceAtomic(table->value).load() != 2) {
            }
            control->padding[0] = table->next->value;
          } else {
            table->next = control->next->next;
            DeviceAtomic(table->value).store(2);
          }
        } else if (mode == 19) {
          auto *node = static_cast<Node *>(input);
          node->next = reinterpret_cast<Node *>(output);
          HostAtomic(node->value).store(1);
          while (HostAtomic(node->value).load() != 2) {
          }
          node->padding[0] = node->next->value;
        }
        return;
      }
      if (mode == 13) {
        auto *p = static_cast<uint32_t *>(input);
        auto *out = reinterpret_cast<uint32_t *>(output);
        sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>
            published(p[0]), acknowledged(p[2]);
        for (unsigned round = 0; round < 32; ++round) {
          const uint32_t token = (uint32_t{1} << 31) + round + 1;
          while (published.load(sycl::memory_order::relaxed) != token) {
          }
          uint32_t expected = 0;
          const bool exchanged = published.compare_exchange_strong(expected, 0, sycl::memory_order::acquire, sycl::memory_order::acquire);
          out[3 * round] = p[1];
          out[3 * round + 1] = expected;
          out[3 * round + 2] = exchanged;
          acknowledged.store(token, sycl::memory_order::release);
        }
      } else if (mode == 14) {
        auto *p = static_cast<WideWord *>(input);
        auto *out = reinterpret_cast<WideWord *>(output);
        sycl::atomic_ref<WideWord, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>
            published(p[0]), acknowledged(p[2]);
        for (unsigned round = 0; round < 32; ++round) {
          const WideWord token = (WideWord{1} << (sizeof(WideWord) * 8 - 1)) + round + 1;
          while (published.load(sycl::memory_order::relaxed) != token) {
          }
          WideWord expected = 0;
          const bool exchanged = published.compare_exchange_strong(expected, 0, sycl::memory_order::acquire, sycl::memory_order::acquire);
          out[3 * round] = p[1];
          out[3 * round + 1] = expected;
          out[3 * round + 2] = exchanged;
          acknowledged.store(token, sycl::memory_order::release);
        }
      } else if (mode == 11) {
        auto *p = static_cast<uint32_t *>(input);
        p[1] = 74;
        sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>(p[0])
            .store(1);
        output[0] = 74;
      } else if (mode == 12) {
        auto *p = static_cast<WideWord *>(input);
        p[1] = 74;
        sycl::atomic_ref<WideWord, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>(p[0])
            .store(1);
        reinterpret_cast<WideWord *>(output)[0] = 74;
      } else if (mode == 9) {
        auto *p = static_cast<uint32_t *>(input);
        sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>
            counter(p[2]);
        output[item.get_global_linear_id()] = counter.fetch_add(1);
      } else if (mode == 10) {
        auto *p = static_cast<WideWord *>(input);
        sycl::atomic_ref<WideWord, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>
            counter(p[2]);
        reinterpret_cast<WideWord *>(output)[item.get_global_linear_id()] = counter.fetch_add(1);
      } else if (mode == 5 || mode == 7) {
        auto *p = static_cast<uint32_t *>(input);
        sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>
            state(p[0]);
        decltype(state) counter(p[2]);
        for (unsigned round = 0; round < 32; ++round) {
          while (state.load(fenced ? sycl::memory_order::relaxed : sycl::memory_order::acquire) != 1) {
          }
          if (fenced) sycl::atomic_fence(sycl::memory_order::acquire, sycl::memory_scope::system);
          counter.fetch_add(1);
          const auto value = p[1];
          output[round] = value;
          p[1] = 2 * value;
          if (fenced) sycl::atomic_fence(sycl::memory_order::release, sycl::memory_scope::system);
          state.store(2, fenced ? sycl::memory_order::relaxed : sycl::memory_order::release);
        }
      } else if (mode == 6 || mode == 8) {
        auto *p = static_cast<WideWord *>(input);
        sycl::atomic_ref<WideWord, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>
            state(p[0]);
        decltype(state) counter(p[2]);
        for (unsigned round = 0; round < 32; ++round) {
          while (state.load(fenced ? sycl::memory_order::relaxed : sycl::memory_order::acquire) != 1) {
          }
          if (fenced) sycl::atomic_fence(sycl::memory_order::acquire, sycl::memory_scope::system);
          counter.fetch_add(1);
          const auto value = p[1];
          reinterpret_cast<WideWord *>(output)[round] = value;
          p[1] = 2 * value;
          if (fenced) sycl::atomic_fence(sycl::memory_order::release, sycl::memory_scope::system);
          state.store(2, fenced ? sycl::memory_order::relaxed : sycl::memory_order::release);
        }
      } else if (mode == 4) {
        auto *p = reinterpret_cast<unsigned char *>(output);
        const auto value = static_cast<uint32_t *>(input)[0];
        p[0] = value + 1;
        p[2] = value + 2;
      } else if (mode == 3) {
        auto *p = static_cast<Node *>(input);
        uint32_t sum = 0;
        while (p) {
          sum += p->value;
          p = p->next;
        }
        output[0] = sum;
      } else {
        const unsigned i = item.get_global_linear_id();
        const unsigned target = mode == 0 ? i : mode == 1 ? i * 17 : indices[i];
        output[i] = static_cast<uint32_t *>(input)[target] + 1;
      }
    });
  }
  void enqueue(sycl::queue &target, void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    if (mode == 6 || mode == 8 || mode == 10 || mode == 12 || mode == 14)
      enqueueTyped<uint64_t>(target, input, output, indices, mode, count);
    else enqueueTyped<uint32_t>(target, input, output, indices, mode, count);
  }
  void submit(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(queue, input, output, indices, mode, count);
  }
  void finish() { queue.wait_and_throw(); }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    submit(input, output, indices, mode, count);
    finish();
  }
  explicit MemoryClient(bool raw = false) : MemoryClient(memory_patterns::allocationKind(concurrencyMode() ? "host" : "shared")) {
    this->raw = raw;
  }
  bool raw = false;
  void *allocate(size_t size) { return raw ? ordinary(size) : allocateBacking(size); }
  void release(void *p) {
    if (raw) std::free(p);
    else releaseBacking(p);
  }
  bool concurrentCapabilities() {
    const auto device = queue.get_device();
    require(device.has(sycl::aspect::usm_host_allocations), "SYCL host USM unavailable");
    require(device.has(sycl::aspect::usm_atomic_host_allocations) && device.has(sycl::aspect::usm_atomic_shared_allocations),
            "SYCL concurrent atomics unavailable");
    return true;
  }
};
} // namespace memory_patterns

int main() {
  try {
    memory_patterns::MemoryClient backend;
    return run(backend);
  } catch (const std::exception &e) {
    std::cerr << e.what() << '\n';
    return 1;
  }
}
