#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 300
#include <algorithm>
#include <array>
#include <cstddef>
#include <cstdint>
#include <limits>
#include <memory>
#include <numeric>
#include <optional>
#include <string>
#include <type_traits>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "opencl/session.hpp"

namespace {
using namespace unified::opencl;

size_t volume(const Shape &shape) { return shape[0] * shape[1] * shape[2]; }

// Visits every work-item in global linear order with its group, local id and the active size of its group.
template <class F> void forEachItem(const Shape &global, const Shape &enqueued, F visit) {
  for (size_t z = 0; z < global[2]; ++z)
    for (size_t y = 0; y < global[1]; ++y)
      for (size_t x = 0; x < global[0]; ++x) {
        const Shape item{x, y, z};
        Shape group{}, local{}, size{};
        for (size_t d = 0; d < 3; ++d) {
          group[d] = item[d] / enqueued[d];
          local[d] = item[d] % enqueued[d];
          size[d] = std::min(enqueued[d], global[d] - group[d] * enqueued[d]);
        }
        visit(x + global[0] * (y + global[1] * z), group, local, size);
      }
}

size_t localLinear(const Shape &local, const Shape &size) { return local[0] + size[0] * (local[1] + size[1] * local[2]); }

constexpr const char *GeometrySource = R"cl(
  __kernel void geometry(__global uint *out, __local uint *tile) {
    uint lid = (uint)get_local_linear_id();
    uint count = (uint)(get_local_size(0) * get_local_size(1) * get_local_size(2));
    tile[lid] = lid + 1;
    barrier(CLK_LOCAL_MEM_FENCE);
    uint sum = 0;
    for (uint i = 0; i < count; ++i) sum += tile[i];
    barrier(CLK_LOCAL_MEM_FENCE);
    tile[count - 1 - lid] = sum + lid;
    barrier(CLK_LOCAL_MEM_FENCE);
    __global uint *row = out + get_global_linear_id() * 16;
    for (uint d = 0; d < 3; ++d) {
      row[d] = (uint)get_local_size(d);
      row[3 + d] = (uint)get_enqueued_local_size(d);
      row[6 + d] = (uint)get_group_id(d);
      row[9 + d] = (uint)get_num_groups(d);
    }
    row[12] = lid;
    row[13] = sum;
    row[14] = tile[lid];
    row[15] = (uint)(get_global_id(0) - get_global_offset(0) + get_global_size(0) * (get_global_id(1) - get_global_offset(1)
              + get_global_size(1) * (get_global_id(2) - get_global_offset(2))));
  })cl";

void geometry(cl_uint dimensions, Shape global, std::optional<Shape> local, Shape offset) {
  Session session;
  session.require(Capability::NonUniformWorkGroups);
  if (local) session.requireGroup(*local);
  auto kernel = session.kernel(GeometrySource, "", "geometry");
  const auto items = volume(global);
  auto out = session.buffer(std::vector<uint32_t>(items * 16, 0xdeadbeefU));
  check(clSetKernelArg(kernel, 0, sizeof(out), &out), "output argument");
  size_t tile = local ? volume(*local) : 0;
  if (!local)
    check(clGetKernelWorkGroupInfo(kernel, session.device, CL_KERNEL_WORK_GROUP_SIZE, sizeof(tile), &tile, nullptr),
          "kernel work-group size");
  check(clSetKernelArg(kernel, 1, tile * sizeof(uint32_t), nullptr), "local argument");
  session.run(kernel, dimensions, global, local ? &*local : nullptr, offset);
  const auto rows = session.read<uint32_t>(out, items * 16);
  const Shape enqueued = local ? *local : Shape{rows[3], rows[4], rows[5]};
  forEachItem(global, enqueued, [&](size_t index, const Shape &group, const Shape &id, const Shape &size) {
    const auto *row = rows.data() + index * 16;
    const auto count = static_cast<uint32_t>(volume(size));
    const auto lid = static_cast<uint32_t>(localLinear(id, size));
    INFO(fmt::format("work-item {} in group ({}, {}, {})", index, group[0], group[1], group[2]));
    for (size_t d = 0; d < 3; ++d) {
      CHECK(row[d] == size[d]);
      CHECK(row[3 + d] == enqueued[d]);
      CHECK(row[6 + d] == group[d]);
      CHECK(row[9 + d] == (global[d] + enqueued[d] - 1) / enqueued[d]);
    }
    CHECK(row[12] == lid);
    CHECK(row[13] == count * (count + 1) / 2);
    CHECK(row[14] == count * (count + 1) / 2 + (count - 1 - lid));
    REQUIRE(row[15] == index);
  });
}

template <class T> struct Collective;
template <> struct Collective<int32_t> {
  static constexpr const char *name = "int";
};
template <> struct Collective<uint32_t> {
  static constexpr const char *name = "uint";
};
template <> struct Collective<int64_t> {
  static constexpr const char *name = "long";
};
template <> struct Collective<uint64_t> {
  static constexpr const char *name = "ulong";
};
template <> struct Collective<float> {
  static constexpr const char *name = "float";
};
template <> struct Collective<double> {
  static constexpr const char *name = "double";
};

constexpr const char *CollectiveSource = R"cl(
  #ifdef FP64
  #pragma OPENCL EXTENSION cl_khr_fp64 : enable
  #endif
  __kernel void collectives(__global const T *in, __global T *out, __global int *flags) {
    size_t g = get_global_linear_id();
    T v = in[g];
    __global T *row = out + g * 10;
    row[0] = work_group_reduce_add(v);
    row[1] = work_group_reduce_min(v);
    row[2] = work_group_reduce_max(v);
    row[3] = work_group_scan_inclusive_add(v);
    row[4] = work_group_scan_inclusive_min(v);
    row[5] = work_group_scan_inclusive_max(v);
    row[6] = work_group_scan_exclusive_add(v);
    row[7] = work_group_scan_exclusive_min(v);
    row[8] = work_group_scan_exclusive_max(v);
  #if DIMENSIONS == 1
    row[9] = work_group_broadcast(v, get_local_size(0) - 1);
  #elif DIMENSIONS == 2
    row[9] = work_group_broadcast(v, get_local_size(0) - 1, get_local_size(1) / 2);
  #else
    row[9] = work_group_broadcast(v, get_local_size(0) - 1, get_local_size(1) / 2, get_local_size(2) - 1);
  #endif
    flags[g * 2] = work_group_all(v > (T)LOWER);
    flags[g * 2 + 1] = work_group_any(v == (T)SPECIAL);
  })cl";

template <class T> T sample(size_t index) {
  const auto value = static_cast<int64_t>((index * 37 + 11) % 61) - 30;
  if constexpr (std::is_unsigned_v<T>) return static_cast<T>(value + 30);
  else return static_cast<T>(value);
}

template <class T> constexpr T lowest() {
  if constexpr (std::is_floating_point_v<T>) return -std::numeric_limits<T>::infinity();
  else return std::numeric_limits<T>::min();
}

template <class T> constexpr T highest() {
  if constexpr (std::is_floating_point_v<T>) return std::numeric_limits<T>::infinity();
  else return std::numeric_limits<T>::max();
}

template <class T> void collectives(cl_uint dimensions, Shape global, Shape local) {
  Session session;
  session.require(Capability::WorkGroupCollectives);
  if constexpr (std::is_same_v<T, double>) session.require(Capability::Fp64);
  if (global[0] % local[0] || global[1] % local[1] || global[2] % local[2]) session.require(Capability::NonUniformWorkGroups);
  session.requireGroup(local);
  constexpr int64_t lower = std::is_unsigned_v<T> ? 0 : -30, special = std::is_unsigned_v<T> ? 7 : -23;
  const auto options = fmt::format("-DT={} -DDIMENSIONS={} -DLOWER={} -DSPECIAL={}{}", Collective<T>::name, dimensions, lower, special,
                                   std::is_same_v<T, double> ? " -DFP64" : "");
  auto kernel = session.kernel(CollectiveSource, options, "collectives");
  const auto items = volume(global);
  std::vector<T> values(items);
  for (size_t i = 0; i < items; ++i)
    values[i] = sample<T>(i);
  auto in = session.buffer(values);
  auto out = session.buffer(std::vector<T>(items * 10, T{}));
  auto flags = session.buffer(std::vector<int32_t>(items * 2, -1));
  check(clSetKernelArg(kernel, 0, sizeof(in), &in), "input argument");
  check(clSetKernelArg(kernel, 1, sizeof(out), &out), "output argument");
  check(clSetKernelArg(kernel, 2, sizeof(flags), &flags), "flag argument");
  session.run(kernel, dimensions, global, &local);
  const auto rows = session.read<T>(out, items * 10);
  const auto predicates = session.read<int32_t>(flags, items * 2);

  // Group members in local linear order, keyed by group linear index.
  const Shape groups{(global[0] + local[0] - 1) / local[0], (global[1] + local[1] - 1) / local[1], (global[2] + local[2] - 1) / local[2]};
  std::vector<std::vector<size_t>> members(volume(groups));
  std::vector<size_t> owner(items);
  std::vector<size_t> broadcastFrom(volume(groups));
  forEachItem(global, local, [&](size_t index, const Shape &group, const Shape &id, const Shape &size) {
    const auto groupIndex = group[0] + groups[0] * (group[1] + groups[1] * group[2]);
    auto &list = members[groupIndex];
    const auto position = localLinear(id, size);
    if (list.size() <= position) list.resize(volume(size));
    list[position] = index;
    owner[index] = groupIndex;
    const Shape source{size[0] - 1, dimensions >= 2 ? size[1] / 2 : 0, dimensions == 3 ? size[2] - 1 : 0};
    if (id == source) broadcastFrom[groupIndex] = index;
  });
  for (size_t groupIndex = 0; groupIndex < members.size(); ++groupIndex) {
    const auto &list = members[groupIndex];
    T sum{}, minimum = highest<T>(), maximum = lowest<T>();
    bool all = true, any = false;
    for (const auto index : list) {
      sum = static_cast<T>(sum + values[index]);
      minimum = std::min(minimum, values[index]);
      maximum = std::max(maximum, values[index]);
      all = all && values[index] > static_cast<T>(lower);
      any = any || values[index] == static_cast<T>(special);
    }
    T inclusiveSum{}, inclusiveMin = highest<T>(), inclusiveMax = lowest<T>();
    for (const auto index : list) {
      const auto *row = rows.data() + index * 10;
      INFO(fmt::format("{} work-item {} in group {}", Collective<T>::name, index, groupIndex));
      CHECK(row[6] == inclusiveSum);
      CHECK(row[7] == inclusiveMin);
      CHECK(row[8] == inclusiveMax);
      inclusiveSum = static_cast<T>(inclusiveSum + values[index]);
      inclusiveMin = std::min(inclusiveMin, values[index]);
      inclusiveMax = std::max(inclusiveMax, values[index]);
      CHECK(row[0] == sum);
      CHECK(row[1] == minimum);
      CHECK(row[2] == maximum);
      CHECK(row[3] == inclusiveSum);
      CHECK(row[4] == inclusiveMin);
      CHECK(row[5] == inclusiveMax);
      CHECK(row[9] == values[broadcastFrom[groupIndex]]);
      CHECK((predicates[index * 2] != 0) == all);
      CHECK((predicates[index * 2 + 1] != 0) == any);
      REQUIRE(owner[index] == groupIndex);
    }
  }
}

constexpr const char *GenericSource = R"cl(
  int bump(int *value, int by) {
    int old = *value;
    *value = old + by;
    return old;
  }
  uint regions(const int *value) {
    return (to_global(value) != NULL) | (to_local(value) != NULL) << 1 | (to_private(value) != NULL) << 2;
  }
  uint fences(const int *value) {
    cl_mem_fence_flags flags = get_fence(value);
    return ((flags & CLK_GLOBAL_MEM_FENCE) != 0) | ((flags & CLK_LOCAL_MEM_FENCE) != 0) << 1;
  }
  void tally(volatile atomic_int *counter) { atomic_fetch_add_explicit(counter, 1, memory_order_relaxed, memory_scope_device); }

  __kernel void pointers(__global int *data, __global uint *out, __global atomic_int *global_count, __local int *tile) {
    __local atomic_int local_count;
    size_t g = get_global_id(0), l = get_local_id(0);
    int own = (int)g * 3;
    tile[l] = (int)l + 100;
    if (l == 0) atomic_init(&local_count, 0);
    barrier(CLK_LOCAL_MEM_FENCE);
    int *candidates[3] = {data + g, tile + l, &own};
    __global uint *row = out + g * 16;
    for (int i = 0; i < 3; ++i) {
      int *p = candidates[i];
      row[i * 4] = (uint)bump(p, i + 1);
      row[i * 4 + 1] = regions(p);
      row[i * 4 + 2] = i < 2 ? fences(p) : 0;
      row[i * 4 + 3] = (uint)*p;
    }
    generic int *chosen = (g & 1) ? (generic int *)(data + g) : (generic int *)(tile + l);
    row[12] = (uint)bump(chosen, 1000);
    row[13] = regions(chosen);
    tally(global_count);
    tally(&local_count);
    barrier(CLK_LOCAL_MEM_FENCE);
    row[14] = (uint)atomic_load(&local_count);
    row[15] = (uint)tile[l];
  })cl";

constexpr const char *GlobalsSource = R"cl(
  typedef struct {
    int a;
    float b;
  } record_t;
  global int counter = 5;
  global uint table[4] = {1, 2, 3, 4};
  global int *pointer = &counter;
  global uint *table_pointer = &table[2];
  global record_t record = {7, 2.5f};
  global int uninitialised;
  global atomic_int hits = ATOMIC_VAR_INIT(0);

  int peek(const int *value) { return *value; }

  kernel void step(global int *out) {
    static global int calls = 0;
    atomic_fetch_add_explicit(&hits, 1, memory_order_relaxed, memory_scope_device);
    if (get_global_id(0) != 0) return;
    out[0] = counter;
    out[1] = *pointer;
    out[2] = (int)table[1];
    out[3] = (int)*table_pointer;
    out[4] = record.a;
    out[5] = (int)record.b;
    out[6] = uninitialised;
    out[7] = calls;
    out[8] = peek(&counter);
    counter += 10;
    *pointer += 1;
    table[1] *= 2;
    *table_pointer += 5;
    record.a += 1;
    uninitialised += 3;
    calls += 1;
  }

  kernel void inspect(global int *out) {
    out[0] = counter;
    out[1] = (int)table[1];
    out[2] = (int)table[2];
    out[3] = record.a;
    out[4] = uninitialised;
    out[5] = atomic_load(&hits);
  })cl";

void globals(size_t items, int launches) {
  Session session;
  session.require(Capability::ProgramScopeGlobals);
  const Shape global{items, 1, 1}, local{std::min<size_t>(items, 32), 1, 1};
  session.requireGroup(local);
  struct Program {
    cl_kernel step, inspect;
    cl_mem out;
  };
  const auto program = [&] {
    auto step = session.kernel(GlobalsSource, "", "step");
    cl_program owner{};
    check(clGetKernelInfo(step, CL_KERNEL_PROGRAM, sizeof(owner), &owner, nullptr), "kernel program");
    size_t total = 0;
    check(clGetProgramBuildInfo(owner, session.device, CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE, sizeof(total), &total, nullptr),
          "global variable total size");
    CHECK(total >= 52);
    cl_int status = CL_SUCCESS;
    auto inspect = clCreateKernel(owner, "inspect", &status);
    check(status, "inspect kernel");
    auto out = session.buffer(std::vector<int32_t>(9, -1));
    check(clSetKernelArg(step, 0, sizeof(out), &out), "step argument");
    check(clSetKernelArg(inspect, 0, sizeof(out), &out), "inspect argument");
    return Program{step, inspect, out};
  };
  const auto first = program(), second = program();
  for (int launch = 0; launch < launches; ++launch) {
    session.run(first.step, 1, global, &local);
    const auto row = session.read<int32_t>(first.out, 9);
    INFO(fmt::format("launch {}", launch));
    CHECK(row[0] == 5 + 11 * launch);
    CHECK(row[1] == 5 + 11 * launch);
    CHECK(row[2] == 2 << launch);
    CHECK(row[3] == 3 + 5 * launch);
    CHECK(row[4] == 7 + launch);
    CHECK(row[5] == 2);
    CHECK(row[6] == 3 * launch);
    CHECK(row[7] == launch);
    CHECK(row[8] == 5 + 11 * launch);
  }
  session.run(first.inspect, 1, {1, 1, 1}, nullptr);
  const auto state = session.read<int32_t>(first.out, 6);
  CHECK(state[0] == 5 + 11 * launches);
  CHECK(state[1] == 2 << launches);
  CHECK(state[2] == 3 + 5 * launches);
  CHECK(state[3] == 7 + launches);
  CHECK(state[4] == 3 * launches);
  CHECK(state[5] == static_cast<int32_t>(items) * launches);
  session.run(second.inspect, 1, {1, 1, 1}, nullptr);
  const auto untouched = session.read<int32_t>(second.out, 6);
  CHECK(untouched == std::vector<int32_t>{5, 2, 3, 7, 0, 0});
  clReleaseKernel(first.inspect);
  clReleaseKernel(second.inspect);
}

void generic(size_t global, size_t local) {
  Session session;
  session.require(Capability::GenericAddressSpace);
  session.requireGroup({local, 1, 1});
  auto kernel = session.kernel(GenericSource, "", "pointers");
  std::vector<int32_t> data(global);
  for (size_t i = 0; i < global; ++i)
    data[i] = static_cast<int32_t>(i * 7) - 50;
  auto dataBuffer = session.buffer(data);
  auto out = session.buffer(std::vector<uint32_t>(global * 16, 0xdeadbeefU));
  auto counter = session.buffer(std::vector<int32_t>{0});
  check(clSetKernelArg(kernel, 0, sizeof(dataBuffer), &dataBuffer), "data argument");
  check(clSetKernelArg(kernel, 1, sizeof(out), &out), "output argument");
  check(clSetKernelArg(kernel, 2, sizeof(counter), &counter), "counter argument");
  check(clSetKernelArg(kernel, 3, local * sizeof(int32_t), nullptr), "local argument");
  const Shape globalShape{global, 1, 1}, localShape{local, 1, 1};
  session.run(kernel, 1, globalShape, &localShape);
  const auto rows = session.read<uint32_t>(out, global * 16);
  const auto updated = session.read<int32_t>(dataBuffer, global);
  CHECK(session.read<int32_t>(counter, 1)[0] == static_cast<int32_t>(global));
  for (size_t g = 0; g < global; ++g) {
    const auto *row = rows.data() + g * 16;
    const auto l = static_cast<int32_t>(g % local);
    const bool odd = g & 1;
    INFO(fmt::format("work-item {}", g));
    CHECK(row[0] == static_cast<uint32_t>(data[g]));
    CHECK(row[1] == 1U);
    CHECK(row[2] == 1U);
    CHECK(row[3] == static_cast<uint32_t>(data[g] + 1));
    CHECK(row[4] == static_cast<uint32_t>(l + 100));
    CHECK(row[5] == 2U);
    CHECK(row[6] == 2U);
    CHECK(row[7] == static_cast<uint32_t>(l + 102));
    CHECK(row[8] == static_cast<uint32_t>(g * 3));
    CHECK(row[9] == 4U);
    CHECK(row[11] == static_cast<uint32_t>(g * 3 + 3));
    CHECK(row[12] == static_cast<uint32_t>(odd ? data[g] + 1 : l + 102));
    CHECK(row[13] == (odd ? 1U : 2U));
    CHECK(row[14] == static_cast<uint32_t>(std::min(local, global - g / local * local)));
    CHECK(row[15] == static_cast<uint32_t>(odd ? l + 102 : l + 1102));
    CHECK(updated[g] == data[g] + (odd ? 1001 : 1));
  }
}
constexpr const char *SystemSource = R"cl(
  typedef struct node {
    global struct node *next;
    int value;
  } node;
  kernel void system_walk(global node *head, global int *out) {
    int sum = 0;
    for (global node *current = head; current; current = current->next)
      sum += current->value;
    out[get_global_id(0)] = sum + (int)get_global_id(0);
  }
  kernel void system_count(global atomic_int *counter, global const int *values) {
    atomic_fetch_add_explicit(counter, values[get_global_id(0)], memory_order_relaxed, memory_scope_all_svm_devices);
  })cl";

struct Node {
  Node *next;
  cl_int value;
};

void systemPointers(size_t nodes, size_t items) {
  Session session;
  cl_device_svm_capabilities svm = 0;
  check(clGetDeviceInfo(session.device, CL_DEVICE_SVM_CAPABILITIES, sizeof(svm), &svm, nullptr), "SVM capabilities");
  if (!(svm & CL_DEVICE_SVM_FINE_GRAIN_SYSTEM)) SKIP("fine-grained system SVM unavailable");
  std::vector<std::unique_ptr<Node>> list;
  for (size_t index = 0; index < nodes; ++index)
    list.push_back(std::make_unique<Node>(Node{nullptr, static_cast<cl_int>(index * 3 + 1)}));
  for (size_t index = 0; index + 1 < nodes; ++index)
    list[(index * 7) % nodes]->next = list[((index + 1) * 7) % nodes].get();
  auto *head = list[0].get();
  std::vector<cl_int> walked(items, -1);
  auto walk = session.kernel(SystemSource, "", "system_walk");
  const cl_bool system = CL_TRUE;
  check(clSetKernelExecInfo(walk, CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM, sizeof(system), &system), "system execution info");
  check(clSetKernelArgSVMPointer(walk, 0, head), "head");
  check(clSetKernelArgSVMPointer(walk, 1, walked.data()), "walk output");
  session.run(walk, 1, {items, 1, 1}, nullptr);
  const auto total = static_cast<cl_int>(3 * nodes * (nodes - 1) / 2 + nodes);
  for (size_t index = 0; index < items; ++index) {
    INFO(fmt::format("work-item {}", index));
    CHECK(walked[index] == total + static_cast<cl_int>(index));
  }

  auto counter = std::make_unique<cl_int>(5);
  std::vector<cl_int> values(items);
  for (size_t index = 0; index < items; ++index)
    values[index] = static_cast<cl_int>(index % 7);
  auto count = session.kernel(SystemSource, "", "system_count");
  check(clSetKernelArgSVMPointer(count, 0, counter.get()), "counter");
  check(clSetKernelArgSVMPointer(count, 1, values.data()), "values");
  session.run(count, 1, {items, 1, 1}, nullptr);
  CHECK(*counter == 5 + std::accumulate(values.begin(), values.end(), 0));
}
constexpr const char *IntelSubgroupSource = R"cl(
  kernel void intel_blocks(global const uint *in, global uint *out, global uint *shuffled, read_only image2d_t source,
                           write_only image2d_t target) {
    uint size = get_max_sub_group_size();
    uint lane = get_sub_group_local_id();
    uint first = (get_group_id(0) * get_num_sub_groups() + get_sub_group_id()) * size;
    uint4 values = intel_sub_group_block_read4(in + first * 4);
    intel_sub_group_block_write4(out + first * 4, values + (uint4)(1));
    uint id = (uint)get_global_id(0);
    shuffled[id] = intel_sub_group_shuffle(id, (lane + 1) % size);
    int2 column = (int2)((int)(get_sub_group_id() * size * sizeof(uint)), 0);
    uint texel = intel_sub_group_block_read(source, column);
    intel_sub_group_block_write(target, column + (int2)(0, 1), texel * 2);
  })cl";

void intelSubgroups(size_t items) {
  Session session;
  session.require(Capability::Images);
  if (!session.extension("cl_intel_subgroups")) SKIP("cl_intel_subgroups unavailable");
  session.requireGroup({items, 1, 1});
  std::vector<cl_uint> input(items * 4);
  std::iota(input.begin(), input.end(), 100u);
  auto in = session.buffer(input);
  auto out = session.buffer(std::vector<cl_uint>(items * 4, 0));
  auto shuffled = session.buffer(std::vector<cl_uint>(items, 0));
  std::vector<cl_uint> texels(items * 2, 0);
  std::iota(texels.begin(), texels.begin() + items, 7u);
  cl_image_desc desc{};
  desc.image_type = CL_MEM_OBJECT_IMAGE2D;
  desc.image_width = items;
  desc.image_height = 2;
  const cl_image_format format{CL_R, CL_UNSIGNED_INT32};
  auto source = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, desc, texels.data());
  auto target = session.image(CL_MEM_WRITE_ONLY, format, desc);
  auto kernel = session.kernel(IntelSubgroupSource, "", "intel_blocks");
  for (cl_uint index = 0; const cl_mem argument : {in, out, shuffled, source, target})
    check(clSetKernelArg(kernel, index++, sizeof(argument), &argument), "argument");
  const Shape group{items, 1, 1};
  session.run(kernel, 1, {items, 1, 1}, &group);
  size_t size = 0;
  check(clGetKernelSubGroupInfo(kernel, session.device, CL_KERNEL_MAX_SUB_GROUP_SIZE_FOR_NDRANGE, sizeof(size_t), &items, sizeof(size),
                                &size, nullptr),
        "subgroup size");
  const auto copied = session.read<cl_uint>(out, items * 4);
  for (size_t index = 0; index < copied.size(); ++index) {
    INFO(fmt::format("block element {}", index));
    CHECK(copied[index] == input[index] + 1);
  }
  const auto rotated = session.read<cl_uint>(shuffled, items);
  for (size_t index = 0; index < items; ++index) {
    INFO(fmt::format("work-item {} with subgroup size {}", index, size));
    CHECK(rotated[index] == index / size * size + (index % size + 1) % size);
  }
  std::vector<cl_uint> written(items);
  const size_t origin[] = {0, 1, 0}, region[] = {items, 1, 1};
  check(clEnqueueReadImage(session.queue, target, CL_TRUE, origin, region, 0, 0, written.data(), 0, nullptr, nullptr), "read target");
  for (size_t index = 0; index < items; ++index) {
    INFO(fmt::format("texel {}", index));
    CHECK(written[index] == (7 + index) * 2);
  }
}
} // namespace

TEST_CASE("edge-cases/runtime/opencl/non-uniform-work-group/1d/n100-l32", "[unified][runtime][compact]") {
  geometry(1, {100, 1, 1}, Shape{32, 1, 1}, {0, 0, 0});
}

TEST_CASE("edge-cases/runtime/opencl/non-uniform-work-group/2d/n37x11-l8x4-o3x1", "[unified][runtime][compact]") {
  geometry(2, {37, 11, 1}, Shape{8, 4, 1}, {3, 1, 0});
}

TEST_CASE("edge-cases/runtime/opencl/non-uniform-work-group/3d/n13x7x5-l4x2x2", "[unified][runtime][compact]") {
  geometry(3, {13, 7, 5}, Shape{4, 2, 2}, {0, 0, 0});
}

TEST_CASE("edge-cases/runtime/opencl/non-uniform-work-group/3d/n13x7x5-implicit", "[unified][runtime][compact]") {
  geometry(3, {13, 7, 5}, std::nullopt, {0, 0, 0});
}

#define UNIFIED_OPENCL_COLLECTIVES(TYPE, NAME)                                                                                             \
  TEST_CASE("edge-cases/runtime/opencl/work-group-collectives/" NAME "/1d/n256-l64", "[unified][runtime][compact]") {                      \
    collectives<TYPE>(1, {256, 1, 1}, {64, 1, 1});                                                                                         \
  }                                                                                                                                        \
  TEST_CASE("edge-cases/runtime/opencl/work-group-collectives/" NAME "/1d/n100-l32", "[unified][runtime][compact]") {                      \
    collectives<TYPE>(1, {100, 1, 1}, {32, 1, 1});                                                                                         \
  }                                                                                                                                        \
  TEST_CASE("edge-cases/runtime/opencl/work-group-collectives/" NAME "/2d/n13x6-l4x4", "[unified][runtime][compact]") {                    \
    collectives<TYPE>(2, {13, 6, 1}, {4, 4, 1});                                                                                           \
  }                                                                                                                                        \
  TEST_CASE("edge-cases/runtime/opencl/work-group-collectives/" NAME "/3d/n6x5x3-l4x2x2", "[unified][runtime][compact]") {                 \
    collectives<TYPE>(3, {6, 5, 3}, {4, 2, 2});                                                                                            \
  }

UNIFIED_OPENCL_COLLECTIVES(int32_t, "i32")
UNIFIED_OPENCL_COLLECTIVES(uint32_t, "u32")
UNIFIED_OPENCL_COLLECTIVES(int64_t, "i64")
UNIFIED_OPENCL_COLLECTIVES(uint64_t, "u64")
UNIFIED_OPENCL_COLLECTIVES(float, "f32")
UNIFIED_OPENCL_COLLECTIVES(double, "f64")

TEST_CASE("edge-cases/runtime/opencl/generic-address-space/pointers/n128-l32", "[unified][runtime][compact]") { generic(128, 32); }

TEST_CASE("edge-cases/runtime/opencl/program-scope-globals/persistence/n64-x3", "[unified][runtime][compact]") { globals(64, 3); }

TEST_CASE("edge-cases/runtime/opencl/svm/fine-grain-system/n64-x96", "[unified][runtime][compact]") { systemPointers(96, 64); }

TEST_CASE("edge-cases/runtime/opencl/intel-subgroups/blocks-and-shuffles/n64", "[unified][runtime][compact]") { intelSubgroups(64); }
