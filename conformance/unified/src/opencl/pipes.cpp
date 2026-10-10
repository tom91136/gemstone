#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 300
#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <numeric>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "opencl/session.hpp"

namespace {
using namespace unified::opencl;

constexpr const char *PipeSource = R"cl(
  typedef struct {
    int id;
    float half_id;
    long squared;
  } packet_t;

  kernel void produce(write_only pipe int out) {
    int value = (int)get_global_id(0);
    write_pipe(out, &value);
  }
  kernel void produce_group(write_only pipe int out) {
    reserve_id_t reserved = work_group_reserve_write_pipe(out, get_local_size(0));
    if (is_valid_reserve_id(reserved)) {
      int value = (int)get_global_id(0);
      write_pipe(out, reserved, get_local_id(0), &value);
      work_group_commit_write_pipe(out, reserved);
    }
  }
  kernel void count(read_only pipe int in, global uint *out) {
    out[0] = get_pipe_num_packets(in);
    out[1] = get_pipe_max_packets(in);
  }
  kernel void consume(read_only pipe int in, global int *out) {
    int value = -1;
    out[get_global_id(0)] = read_pipe(in, &value) == 0 ? value : -1;
  }
  kernel void consume_reserved(read_only pipe int in, global int *out) {
    int value = -1;
    reserve_id_t reserved = reserve_read_pipe(in, 1);
    if (is_valid_reserve_id(reserved)) {
      read_pipe(in, reserved, 0, &value);
      commit_read_pipe(in, reserved);
    }
    out[get_global_id(0)] = value;
  }
  kernel void produce_struct(write_only pipe packet_t out) {
    int id = (int)get_global_id(0);
    packet_t packet = {id, id * 0.5f, (long)id * id};
    write_pipe(out, &packet);
  }
  kernel void consume_struct(read_only pipe packet_t in, global long *out) {
    packet_t packet;
    if (read_pipe(in, &packet) == 0) out[packet.id] = packet.squared + (long)(packet.half_id * 4.0f);
  })cl";

cl_mem createPipe(Session &session, cl_uint packetSize, cl_uint packets) {
  cl_int status = CL_SUCCESS;
  auto pipe = clCreatePipe(session.context, CL_MEM_READ_WRITE | CL_MEM_HOST_NO_ACCESS, packetSize, packets, nullptr, &status);
  check(status, "pipe");
  return pipe;
}

void run(Session &session, const char *name, cl_mem pipe, cl_mem out, size_t items, size_t local) {
  auto kernel = session.kernel(PipeSource, "", name);
  check(clSetKernelArg(kernel, 0, sizeof(pipe), &pipe), "pipe argument");
  if (out) check(clSetKernelArg(kernel, 1, sizeof(out), &out), "output argument");
  const Shape group{local, 1, 1};
  session.run(kernel, 1, {items, 1, 1}, &group);
}

void producerConsumer(const char *producer, const char *consumer) {
  Session session;
  session.require(Capability::Pipes);
  constexpr size_t items = 64, local = 16;
  auto pipe = createPipe(session, sizeof(int32_t), items);
  run(session, producer, pipe, nullptr, items, local);
  auto counts = session.buffer(std::vector<uint32_t>(2, 0));
  run(session, "count", pipe, counts, 1, 1);
  const auto reported = session.read<uint32_t>(counts, 2);
  CHECK(reported[0] == items);
  CHECK(reported[1] >= items);
  auto out = session.buffer(std::vector<int32_t>(items, -2));
  run(session, consumer, pipe, out, items, local);
  auto values = session.read<int32_t>(out, items);
  std::sort(values.begin(), values.end());
  std::vector<int32_t> expected(items);
  std::iota(expected.begin(), expected.end(), 0);
  CHECK(values == expected);
  clReleaseMemObject(pipe);
}

void structPackets() {
  Session session;
  session.require(Capability::Pipes);
  constexpr size_t items = 32;
  auto pipe = createPipe(session, 16, items);
  run(session, "produce_struct", pipe, nullptr, items, 8);
  auto out = session.buffer(std::vector<int64_t>(items, -1));
  run(session, "consume_struct", pipe, out, items, 8);
  const auto values = session.read<int64_t>(out, items);
  for (size_t id = 0; id < items; ++id) {
    INFO(fmt::format("packet {}", id));
    CHECK(values[id] == static_cast<int64_t>(id * id + id * 2));
  }
  clReleaseMemObject(pipe);
}
} // namespace

TEST_CASE("edge-cases/runtime/opencl/pipes/write-read/n64", "[unified][runtime][compact]") { producerConsumer("produce", "consume"); }

TEST_CASE("edge-cases/runtime/opencl/pipes/reservations/n64", "[unified][runtime][compact]") {
  producerConsumer("produce_group", "consume_reserved");
}

TEST_CASE("edge-cases/runtime/opencl/pipes/struct-packets/n32", "[unified][runtime][compact]") { structPackets(); }
