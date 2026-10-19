#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 300
#include <cstddef>
#include <cstdint>
#include <vector>

#include <CL/cl.h>
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include "opencl/session.hpp"

using namespace aspartame;

namespace {
using namespace unified::opencl;

constexpr const char *EnqueueSource = R"cl(
  kernel void spawn_ndrange(global int *out, int bias) {
    queue_t queue = get_default_queue();
    int status = enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange_1D(64, 16), ^{
      out[get_global_id(0)] = (int)get_global_id(0) * 3 + bias + (int)get_local_size(0);
    });
    size_t sizes[2] = {8, 4}, groups[2] = {4, 2}, origin[2] = {2, 1};
    status |= enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange_2D(origin, sizes, groups), ^{
      out[64 + (get_global_id(1) - 1) * 8 + get_global_id(0) - 2] = (int)(get_group_id(0) * 10 + get_group_id(1));
    });
    out[96] = status;
  }

  kernel void spawn_each(global int *out) {
    size_t id = get_global_id(0);
    out[id] = (int)id;
    barrier(CLK_GLOBAL_MEM_FENCE);
    if (get_local_id(0) == 0) {
      size_t first = id, count = get_local_size(0);
      enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_WAIT_WORK_GROUP, ndrange_1D(1), ^{
        int sum = 0;
        for (size_t i = 0; i < count; ++i) sum += out[first + i];
        out[64 + first / count] = sum;
      });
    }
    enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(1), ^{ out[128 + id] = (int)id * 2; });
  }

  kernel void spawn_ordered(global int *out) {
    queue_t queue = get_default_queue();
    clk_event_t first, second, marker, gate = create_user_event();
    enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(1), 0, NULL, &first, ^{
      enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange_1D(1), ^{ out[0] = 7; });
    });
    enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(1), 1, &first, &second, ^{ out[1] = out[0]; });
    clk_event_t both[2] = {first, second};
    enqueue_marker(queue, 2, both, &marker);
    clk_event_t gated[2] = {marker, gate};
    enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(1), 2, gated, NULL, ^{ out[2] = out[1] + 1; });
    out[3] = is_valid_event(gate);
    retain_event(first);
    release_event(first);
    release_event(first);
    release_event(second);
    release_event(marker);
    set_user_event_status(gate, CL_COMPLETE);
    release_event(gate);
  }

  kernel void spawn_local(global int *out) {
    enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange_1D(32, 16),
                   ^(local void *first, local void *second) {
                     local int *a = (local int *)first;
                     local long *b = (local long *)second;
                     size_t lid = get_local_id(0);
                     a[lid] = (int)get_global_id(0);
                     b[lid] = 100 * (long)get_global_id(0);
                     work_group_barrier(CLK_LOCAL_MEM_FENCE);
                     out[get_global_id(0)] = a[(lid + 1) % 16] + (int)b[15 - lid];
                   },
                   16u * sizeof(int), 16u * sizeof(long));
  }

  void descend(global int *out, int level) {
    atomic_inc(&out[level]);
    if (level + 1 < 8)
      enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange_1D(2), ^{ descend(out, level + 1); });
  }
  kernel void spawn_nested(global int *out) { descend(out, 0); }

  kernel void spawn_on(queue_t queue, global int *out) {
    out[1] = enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(4), ^{ atomic_add(&out[0], (int)get_global_id(0) + 1); });
  }

  kernel void spawn_sized(global int *out) {
    void (^block)(void) = ^{ out[3 + get_global_id(0)] = (int)get_local_size(0); };
    size_t size = get_kernel_work_group_size(block);
    size_t multiple = get_kernel_preferred_work_group_size_multiple(block);
    out[0] = (int)size;
    out[1] = (int)multiple;
    out[2] = enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange_1D(size * 2, size), block);
  }

  kernel void spawn_profiled(global ulong *out) {
    clk_event_t done;
    enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(16), 0, NULL, &done, ^{ out[get_global_id(0)] = 1; });
    enqueue_kernel(get_default_queue(), CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(1), 1, &done, NULL, ^{
      capture_event_profiling_info(done, CLK_PROFILING_COMMAND_EXEC_TIME, out + 16);
      release_event(done);
    });
  }
)cl";

class Enqueue {
public:
  Session session;
  cl_command_queue device{};

  Enqueue() {
    if (!session.cl20()) {
      cl_device_device_enqueue_capabilities capabilities = 0;
      check(clGetDeviceInfo(session.device, CL_DEVICE_DEVICE_ENQUEUE_CAPABILITIES, sizeof(capabilities), &capabilities, nullptr),
            "device enqueue capabilities");
      if (!(capabilities & CL_DEVICE_QUEUE_SUPPORTED)) SKIP("device-side enqueue unavailable");
    }
    device = queue(true);
  }

  ~Enqueue() {
    for (auto *created : queues)
      clReleaseCommandQueue(created);
  }

  cl_command_queue queue(bool fallback) {
    const cl_command_queue_properties flags = CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE | CL_QUEUE_ON_DEVICE
                                              | (fallback ? CL_QUEUE_ON_DEVICE_DEFAULT : 0);
    const cl_queue_properties properties[] = {CL_QUEUE_PROPERTIES, flags, 0};
    cl_int status = CL_SUCCESS;
    auto *created = clCreateCommandQueueWithProperties(session.context, session.device, properties, &status);
    check(status, "device queue");
    queues.push_back(created);
    return created;
  }

  cl_kernel kernel(const char *name) { return session.kernel(EnqueueSource, "", name); }

  template <class T> std::vector<T> launch(const char *name, const std::vector<T> &initial, size_t items, size_t local) {
    auto *kernel = this->kernel(name);
    auto out = session.buffer(initial);
    arg(kernel, 0, out, "output argument");
    const Shape group{local, 1, 1};
    session.run(kernel, 1, {items, 1, 1}, &group);
    return session.read<T>(out, initial.size());
  }

private:
  std::vector<cl_command_queue> queues;
};

void ndrange() {
  Enqueue enqueue;
  auto *kernel = enqueue.kernel("spawn_ndrange");
  auto *out = enqueue.session.buffer(std::vector<int32_t>(97, -1));
  const cl_int bias = 5;
  arg(kernel, 0, out, "output argument");
  arg(kernel, 1, bias, "bias argument");
  const Shape one{1, 1, 1};
  enqueue.session.run(kernel, 1, one, &one);
  const auto values = enqueue.session.read<int32_t>(out, 97);
  for (int id = 0; id < 64; ++id) {
    INFO(fmt::format("1D work-item {}", id));
    CHECK(values[id] == (id * 3) + bias + 16);
  }
  for (int y = 0; y < 4; ++y)
    for (int x = 0; x < 8; ++x) {
      INFO(fmt::format("2D work-item {}x{}", x, y));
      CHECK(values[64 + (y * 8) + x] == (x / 4 * 10) + (y / 2));
    }
  CHECK(values[96] == 0);
}

void each() {
  Enqueue enqueue;
  const auto values = enqueue.launch("spawn_each", std::vector<int32_t>(192, -1), 64, 16);
  for (int group = 0; group < 4; ++group) {
    INFO(fmt::format("group {}", group));
    CHECK(values[64 + group] == (16 * (group * 16)) + 120);
  }
  for (int id = 0; id < 64; ++id) {
    INFO(fmt::format("work-item {}", id));
    CHECK(values[id] == id);
    CHECK(values[128 + id] == id * 2);
  }
}

void ordered() {
  Enqueue enqueue;
  const auto values = enqueue.launch("spawn_ordered", std::vector<int32_t>(4, -1), 1, 1);
  CHECK(values == std::vector<int32_t>{7, 7, 8, 1});
}

void local() {
  Enqueue enqueue;
  const auto values = enqueue.launch("spawn_local", std::vector<int32_t>(32, -1), 1, 1);
  for (int id = 0; id < 32; ++id) {
    INFO(fmt::format("work-item {}", id));
    const int base = id / 16 * 16;
    const int lid = id % 16;
    CHECK(values[id] == base + ((lid + 1) % 16) + (100 * (base + 15 - lid)));
  }
}

void nested() {
  Enqueue enqueue;
  const auto values = enqueue.launch("spawn_nested", std::vector<int32_t>(8, 0), 1, 1);
  for (int level = 0; level < 8; ++level) {
    INFO(fmt::format("level {}", level));
    CHECK(values[level] == 1 << level);
  }
}

void argument() {
  Enqueue enqueue;
  cl_uint queues = 0;
  check(clGetDeviceInfo(enqueue.session.device, CL_DEVICE_MAX_ON_DEVICE_QUEUES, sizeof(queues), &queues, nullptr), "device queue limit");
  auto *other = queues > 1 ? enqueue.queue(false) : enqueue.device;
  auto *kernel = enqueue.kernel("spawn_on");
  auto *out = enqueue.session.buffer(std::vector<int32_t>{0, -1});
  arg(kernel, 0, other, "queue argument");
  arg(kernel, 1, out, "output argument");
  const Shape one{1, 1, 1};
  enqueue.session.run(kernel, 1, one, &one);
  CHECK(enqueue.session.read<int32_t>(out, 2) == std::vector<int32_t>{10, 0});
}

void sized() {
  Enqueue enqueue;
  size_t limit = 0;
  check(clGetDeviceInfo(enqueue.session.device, CL_DEVICE_MAX_WORK_GROUP_SIZE, sizeof(limit), &limit, nullptr), "work-group limit");
  const auto values = enqueue.launch("spawn_sized", std::vector<int32_t>(3 + (2 * limit), -1), 1, 1);
  const auto size = values[0];
  REQUIRE(size >= 1);
  CHECK(std::cmp_less_equal(size, limit));
  CHECK(values[1] >= 1);
  CHECK(values[2] == 0);
  for (int id = 0; id < size * 2; ++id) {
    INFO(fmt::format("work-item {}", id));
    CHECK(values[3 + id] == size);
  }
}

void profiled() {
  Enqueue enqueue;
  const auto values = enqueue.launch("spawn_profiled", std::vector<uint64_t>(18, 0), 1, 1);
  CHECK((values ^ take(16)) == std::vector<uint64_t>(16, 1));
  CHECK(values[16] > 0);
  CHECK(values[17] >= values[16]);
}
} // namespace

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/ndrange/n64-and-8x4", "[unified][runtime][compact]") { ndrange(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/per-work-item/n64-l16", "[unified][runtime][compact]") { each(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/events/markers-and-user-events", "[unified][runtime][compact]") { ordered(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/local-arguments/n32-l16", "[unified][runtime][compact]") { local(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/nested/depth8", "[unified][runtime][compact]") { nested(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/queue-argument/n4", "[unified][runtime][compact]") { argument(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/work-group-size/queries", "[unified][runtime][compact]") { sized(); }

TEST_CASE("edge-cases/runtime/opencl/device-enqueue/profiling/capture", "[unified][runtime][compact]") { profiled(); }
