#include <atomic>
#include <cassert>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <map>
#include <string>
#include <thread>
#include <vector>

#include <dlfcn.h>
#include <unistd.h>

#include <arpa/inet.h>
#include <sys/socket.h>

// Exercise the actual generated ABI and transport against a bounded independent memory peer.
// The peer never computes BLAS or implements optional C output arguments.
class Peer {
  static uint32_t u32(const uint8_t *p) { return uint32_t(p[0]) << 24 | uint32_t(p[1]) << 16 | uint32_t(p[2]) << 8 | p[3]; }
  static uint64_t u64(const uint8_t *p) { return uint64_t(u32(p)) << 32 | u32(p + 4); }
  static void put32(std::vector<uint8_t> &v, uint32_t x) {
    for (int i = 3; i >= 0; --i)
      v.push_back(uint8_t(x >> (i * 8)));
  }
  static void put64(std::vector<uint8_t> &v, uint64_t x) {
    put32(v, uint32_t(x >> 32));
    put32(v, uint32_t(x));
  }
  static bool read(int fd, uint8_t *p, size_t n) {
    while (n) {
      auto k = recv(fd, p, n, 0);
      if (k <= 0) return false;
      p += k;
      n -= size_t(k);
    }
    return true;
  }
  static void send_bytes(int fd, const std::vector<uint8_t> &v) {
    size_t offset = 0;
    while (offset < v.size()) {
      auto n = send(fd, v.data() + offset, v.size() - offset, MSG_NOSIGNAL);
      assert(n > 0);
      offset += size_t(n);
    }
  }
  int listener_ = -1;
  std::atomic_int active_{-1};
  std::atomic_bool stopped_{false}, drop_{false};
  std::atomic_uint connections_{0}, globals_{0};
  std::atomic_uint64_t next_context_{81}, last_context_{0};
  std::thread worker_;
  std::map<uint64_t, std::vector<uint8_t>> memory_;
  std::atomic_uint64_t next_{0x100000000ULL};
  void serve(int fd) {
    uint8_t header[184];
    while (read(fd, header, sizeof(header))) {
      assert(u32(header) == 0x474d5331 && u32(header + 4) == 4);
      assert(std::memcmp(header + 72, token, 64) == 0);
      last_context_.store(u64(header + 152));
      const auto name_size = u32(header + 176), size = u32(header + 180);
      assert(name_size > 0 && name_size <= 128 && size <= 4096);
      std::vector<uint8_t> name_bytes(name_size), payload(size), result;
      assert(read(fd, name_bytes.data(), name_size) && read(fd, payload.data(), size));
      if (drop_.exchange(false)) return;
      const std::string name(name_bytes.begin(), name_bytes.end());
      if (name == "cuInit" || name == "cuCtxDestroy_v2" || name == "cuModuleUnload") {
      } else if (name == "cuCtxCreate_v2") put64(result, next_context_.fetch_add(1));
      else if (name == "cuModuleLoadData") put64(result, 91);
      else if (name == "cuModuleGetGlobal_v2") {
        assert(u64(payload.data()) == 91);
        ++globals_;
        put64(result, 0x100080000ULL);
        put64(result, 8);
      } else if (name == "__cudaGemstoneResolveFunctions") {
        assert(u64(payload.data()) == 91 && size >= 16);
        const auto names_size = u32(payload.data() + 8);
        const auto count = u32(payload.data() + 12);
        assert(names_size + 12 == size && count <= 4096);
        put32(result, 4 + count * 12);
        put32(result, count);
        size_t offset = 16;
        for (uint32_t index = 0; index < count; ++index) {
          const auto entry_size = u32(payload.data() + offset);
          assert(entry_size > 0 && offset + 4 + entry_size <= size);
          offset += 4 + entry_size;
          put64(result, 100 + index);
          put32(result, 0);
        }
        assert(offset == size);
      } else if (name == "cuMemAlloc_v2") {
        const auto n = u64(payload.data());
        assert(n <= 64);
        memory_[next_].resize(n);
        put64(result, next_);
        next_ += 4096;
      } else if (name == "cuMemFree_v2") assert(memory_.erase(u64(payload.data())) == 1);
      else if (name == "cuMemcpyHtoD_v2") {
        auto &out = memory_.at(u64(payload.data()));
        const auto n = u32(payload.data() + 8);
        assert(n == out.size());
        assert(size == 20 + n);
        std::memcpy(out.data(), payload.data() + 12, n);
      } else if (name == "cuMemcpyDtoH_v2") {
        auto &in = memory_.at(u64(payload.data()));
        assert(u64(payload.data() + 8) == in.size());
        put32(result, uint32_t(in.size()));
        result.insert(result.end(), in.begin(), in.end());
      } else {
        std::fprintf(stderr, "unexpected CUDA request %s\n", name.c_str());
        std::abort();
      }
      std::vector<uint8_t> response;
      put32(response, 0x474d5231);
      put32(response, 4);
      put32(response, 0);
      put32(response, 0);
      put32(response, uint32_t(result.size()));
      response.insert(response.end(), result.begin(), result.end());
      send_bytes(fd, response);
    }
  }

public:
  static constexpr char token[] = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
  Peer() {
    listener_ = socket(AF_INET, SOCK_STREAM, 0);
    assert(listener_ >= 0);
    sockaddr_in a{};
    a.sin_family = AF_INET;
    a.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    assert(bind(listener_, reinterpret_cast<sockaddr *>(&a), sizeof(a)) == 0);
    assert(listen(listener_, 4) == 0);
    socklen_t n = sizeof(a);
    assert(getsockname(listener_, reinterpret_cast<sockaddr *>(&a), &n) == 0);
    auto port = std::to_string(ntohs(a.sin_port));
    setenv("GEMSTONE_RPC_PORT", port.c_str(), 1);
    setenv("GEMSTONE_RPC_TOKEN", token, 1);
    worker_ = std::thread([this] {
      while (!stopped_) {
        int fd = accept(listener_, nullptr, nullptr);
        if (fd < 0) break;
        active_ = fd;
        ++connections_;
        memory_.clear();
        next_ = 0x100000000ULL;
        timeval t{3, 0};
        setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &t, sizeof(t));
        serve(fd);
        close(fd);
        active_ = -1;
      }
    });
  }
  ~Peer() {
    stopped_ = true;
    shutdown(listener_, SHUT_RDWR);
    if (active_ >= 0) shutdown(active_, SHUT_RDWR);
    worker_.join();
    close(listener_);
  }
  void dropNext() { drop_ = true; }
  void nextAllocation(uint64_t address) { next_ = address; }
  unsigned connections() { return connections_.load(); }
  unsigned globals() { return globals_.load(); }
  uint64_t lastContext() { return last_context_.load(); }
};

template <class T> T symbol(void *library, const char *name) {
  auto value = dlsym(library, name);
  assert(value != nullptr);
  return reinterpret_cast<T>(value);
}
int main(int argc, char **argv) {
  assert(argc == 2);
  Peer peer;
  void *library = dlopen("libcuda.so.1", RTLD_NOW | RTLD_LOCAL);
  assert(library);
  auto init = symbol<int (*)(unsigned)>(library, "cuInit");
  auto create = symbol<int (*)(void **, unsigned, int)>(library, "cuCtxCreate_v2");
  auto allocate = symbol<int (*)(uint64_t *, size_t)>(library, "cuMemAlloc_v2");
  auto release = symbol<int (*)(uint64_t)>(library, "cuMemFree_v2");
  auto write = symbol<int (*)(uint64_t, const void *, size_t)>(library, "cuMemcpyHtoD_v2");
  auto read = symbol<int (*)(void *, uint64_t, size_t)>(library, "cuMemcpyDtoH_v2");
  auto load = symbol<int (*)(void **, const void *)>(library, "cuModuleLoadData");
  assert(init(0) == 0);
  void *context = nullptr;
  assert(create(&context, 0, 0) == 0);
  const char *ptx = ".version 8.0\n.target sm_80\n.address_size 64\n.global .u32 global_value[2];\n.visible .entry k(){ret;}";
  if (std::strcmp(argv[1], "context-routing") == 0) {
    uint64_t first_pointer = 0;
    assert(allocate(&first_pointer, 4) == 0);
    assert(peer.lastContext() == reinterpret_cast<uint64_t>(context));
    void *second = nullptr;
    assert(create(&second, 0, 0) == 0 && second != context);
    uint64_t second_pointer = 0;
    assert(allocate(&second_pointer, 4) == 0);
    assert(peer.lastContext() == reinterpret_cast<uint64_t>(second));
    auto set_current = symbol<int (*)(void *)>(library, "cuCtxSetCurrent");
    assert(set_current(context) == 0);
    uint64_t restored_pointer = 0;
    assert(allocate(&restored_pointer, 4) == 0);
    assert(peer.lastContext() == reinterpret_cast<uint64_t>(context));
    assert(set_current(reinterpret_cast<void *>(UINTPTR_MAX)) != 0);
    auto primary_retain = symbol<int (*)(void **, int)>(library, "cuDevicePrimaryCtxRetain");
    auto primary_release = symbol<int (*)(int)>(library, "cuDevicePrimaryCtxRelease_v2");
    void *primary_a = nullptr, *primary_b = nullptr;
    assert(primary_retain(&primary_a, 0) == 0);
    assert(primary_retain(&primary_b, 0) == 0);
    assert(primary_a == primary_b);
    assert(primary_release(0) == 0);
    assert(set_current(primary_a) == 0);
    assert(primary_release(0) == 0);
    assert(set_current(primary_a) != 0);
  } else if (std::strcmp(argv[1], "session-loss") == 0) {
    uint64_t pointer = 0;
    void *module = nullptr;
    assert(allocate(&pointer, 4) == 0);
    assert(load(&module, ptx) == 0);
    peer.dropNext();
    float value = 0;
    assert(read(&value, pointer, 4) != 0);
    uint64_t fresh = 0;
    assert(allocate(&fresh, 4) != 0);
    assert(load(&module, ptx) != 0);
    assert(peer.connections() == 1);
  } else if (std::strcmp(argv[1], "global-outputs") == 0) {
    void *module = nullptr;
    assert(load(&module, ptx) == 0);
    auto global = symbol<int (*)(uint64_t *, size_t *, void *, const char *)>(library, "cuModuleGetGlobal_v2");
    uint64_t pointer = 0;
    size_t bytes = 0;
    assert(global(&pointer, &bytes, module, "global_value") == 0 && pointer == 0x100080000ULL && bytes == 8);
    bytes = 0;
    assert(global(nullptr, &bytes, module, "global_value") == 0 && bytes == 8);
    pointer = 0;
    assert(global(&pointer, nullptr, module, "global_value") == 0 && pointer == 0x100080000ULL);
    assert(global(nullptr, nullptr, module, "global_value") != 0);
    assert(peer.globals() == 3);
  } else {
    auto blas_create = symbol<int (*)(void **)>(library, "cublasCreate_v2");
    auto mode = symbol<int (*)(void *, int)>(library, "cublasSetPointerMode_v2");
    auto destroy = symbol<int (*)(void *)>(library, "cublasDestroy_v2");
    void *handle = nullptr;
    assert(blas_create(&handle) == 0);
    const bool half = std::strcmp(argv[1], "cublas-half") == 0;
    const size_t scalar_size = half ? 2 : 4;
    uint64_t dx, dy, dc, da, db;
    for (auto *p : {&dx, &dy, &dc})
      assert(allocate(p, scalar_size) == 0);
    // Scalar mode must govern even addresses outside the old pointer heuristic.
    peer.nextAllocation(0x2000000000ULL);
    for (auto *p : {&da, &db})
      assert(allocate(p, scalar_size) == 0);
    if (half) {
      const uint16_t a = 0x4200, b = 0x4400, c = 0x4500, alpha = 0x4000, beta = 0x4200;
      assert(write(dx, &a, 2) == 0 && write(dy, &b, 2) == 0 && write(da, &alpha, 2) == 0 && write(db, &beta, 2) == 0);
      auto gemm = symbol<int (*)(void *, int, int, int, int, int, const uint16_t *, const uint16_t *, int, const uint16_t *, int,
                                 const uint16_t *, uint16_t *, int)>(library, "cublasHgemm");
      for (int pointer_mode : {0, 1}) {
        assert(mode(handle, pointer_mode) == 0 && write(dc, &c, 2) == 0);
        assert(gemm(handle, 0, 0, 1, 1, 1, pointer_mode ? reinterpret_cast<uint16_t *>(da) : &alpha, reinterpret_cast<uint16_t *>(dx), 1,
                    reinterpret_cast<uint16_t *>(dy), 1, pointer_mode ? reinterpret_cast<uint16_t *>(db) : &beta,
                    reinterpret_cast<uint16_t *>(dc), 1)
               == 0);
        uint16_t observed = 0;
        assert(read(&observed, dc, 2) == 0 && observed == 0x50e0);
      }
      for (auto p : {dx, dy, dc, da, db})
        assert(release(p) == 0);
      assert(destroy(handle) == 0);
      dlclose(library);
      return 0;
    }
    const float a = 3, b = 4, c = 5, alpha = 2, beta = 3;
    assert(write(dx, &a, 4) == 0 && write(dy, &b, 4) == 0 && write(dc, &c, 4) == 0 && write(da, &alpha, 4) == 0
           && write(db, &beta, 4) == 0);
    for (int pointer_mode : {0, 1}) {
      assert(mode(handle, pointer_mode) == 0);
      assert(write(dy, &b, 4) == 0 && write(dc, &c, 4) == 0);
      const float *ap = pointer_mode ? reinterpret_cast<float *>(da) : &alpha;
      const float *bp = pointer_mode ? reinterpret_cast<float *>(db) : &beta;
      float observed = 0;
      if (std::strcmp(argv[1], "cublas-axpy") == 0) {
        auto axpy = symbol<int (*)(void *, int, const float *, const float *, int, float *, int)>(library, "cublasSaxpy_v2");
        assert(axpy(handle, 1, ap, reinterpret_cast<float *>(dx), 1, reinterpret_cast<float *>(dy), 1) == 0);
        assert(read(&observed, dy, 4) == 0 && observed == 10);
      } else if (std::strcmp(argv[1], "cublas-scal") == 0) {
        auto scal = symbol<int (*)(void *, int, const float *, float *, int)>(library, "cublasSscal_v2");
        assert(scal(handle, 1, ap, reinterpret_cast<float *>(dy), 1) == 0);
        assert(read(&observed, dy, 4) == 0 && observed == 8);
      } else if (std::strcmp(argv[1], "cublas-gemv") == 0) {
        auto gemv =
            symbol<int (*)(void *, int, int, int, const float *, const float *, int, const float *, int, const float *, float *, int)>(
                library, "cublasSgemv_v2");
        assert(gemv(handle, 0, 1, 1, ap, reinterpret_cast<float *>(dx), 1, reinterpret_cast<float *>(dy), 1, bp,
                    reinterpret_cast<float *>(dc), 1)
               == 0);
        assert(read(&observed, dc, 4) == 0 && observed == 39);
      } else if (std::strcmp(argv[1], "cublas-results") == 0) {
        auto dot = symbol<int (*)(void *, int, const float *, int, const float *, int, float *)>(library, "cublasSdot_v2");
        float *output = pointer_mode ? reinterpret_cast<float *>(da) : &observed;
        assert(dot(handle, 1, reinterpret_cast<float *>(dx), 1, reinterpret_cast<float *>(dy), 1, output) == 0);
        if (pointer_mode) assert(read(&observed, da, 4) == 0);
        assert(observed == 12);
        for (const char *name : {"cublasSnrm2_v2", "cublasSasum_v2"}) {
          auto reduce = symbol<int (*)(void *, int, const float *, int, float *)>(library, name);
          assert(reduce(handle, 1, reinterpret_cast<float *>(dx), 1, output) == 0);
          if (pointer_mode) assert(read(&observed, da, 4) == 0);
          assert(observed == 3);
          assert(reduce(handle, 0, nullptr, 1, output) == 0);
          if (pointer_mode) assert(read(&observed, da, 4) == 0);
          assert(observed == 0);
        }
      } else {
        auto gemm = symbol<int (*)(void *, int, int, int, int, int, const float *, const float *, int, const float *, int, const float *,
                                   float *, int)>(library, "cublasSgemm_v2");
        assert(gemm(handle, 0, 0, 1, 1, 1, ap, reinterpret_cast<float *>(dx), 1, reinterpret_cast<float *>(dy), 1, bp,
                    reinterpret_cast<float *>(dc), 1)
               == 0);
        assert(read(&observed, dc, 4) == 0 && observed == 39);
      }
    }
    for (auto p : {dx, dy, dc, da, db}) {
      assert(release(p) == 0);
    }
    assert(destroy(handle) == 0);
  }
  dlclose(library);
  return 0;
}
