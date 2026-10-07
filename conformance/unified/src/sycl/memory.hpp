#pragma once
#include <sycl/sycl.hpp>

#include "memory_support.hpp"

namespace memory_patterns {
struct Backend {
  std::string kind;
  explicit Backend(std::string kind) : kind(std::move(kind)) {}
  sycl::queue queue{sycl::gpu_selector_v};
  void *allocate(size_t size) {
    auto *p = (kind == "host") ? sycl::aligned_alloc_host(64, size, queue) : sycl::aligned_alloc_shared(64, size, queue);
    require(p, "SYCL shared allocation");
    return p;
  }
  void release(void *p) { sycl::free(p, queue); }
  template <class WideWord>
  void enqueueTyped(sycl::queue &target, void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    target.parallel_for(sycl::nd_range<1>(count, count == 1 ? 1 : 32), [=](sycl::nd_item<1> item) {
      const bool fenced = mode == 7 || mode == 8;
      if (mode == 19) {
        auto *node = static_cast<Node *>(input);
        using HostAtomic =
            sycl::atomic_ref<uint32_t, sycl::memory_order::acq_rel, sycl::memory_scope::system, sycl::access::address_space::global_space>;
        node->next = reinterpret_cast<Node *>(output);
        HostAtomic(node->value).store(1);
        while (HostAtomic(node->value).load() != 2) {
        }
        node->padding[0] = node->next->value;
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
    if (mode == 6 || mode == 8 || mode == 10 || mode == 14) enqueueTyped<uint64_t>(target, input, output, indices, mode, count);
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
  void ordered(uint32_t *input, uint32_t *middle, uint32_t *output, unsigned count, bool crossQueue) {
    sycl::queue second(queue.get_context(), queue.get_device());
    const auto produced = queue.parallel_for(sycl::range<1>(count), [=](sycl::id<1> i) { middle[i] = input[i] + 1; });
    auto &consumer = crossQueue ? second : queue;
    auto consumed = consumer.submit([&](sycl::handler &handler) {
      handler.depends_on(produced);
      handler.parallel_for(sycl::range<1>(count), [=](sycl::id<1> i) { output[i] = middle[i] + 1; });
    });
    consumed.wait_and_throw();
  }
};

} // namespace memory_patterns
