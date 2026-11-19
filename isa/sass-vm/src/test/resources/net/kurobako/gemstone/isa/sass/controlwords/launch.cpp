#include <cuda.h>

#include <chrono>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <fstream>
#include <iterator>
#include <string>
#include <thread>
#include <vector>

// usage: launch <cubin> <kernel> <block> <grid> <sets> <repeats> <seed> <arg>...
// arg: u32:lo:hi i32:lo:hi u64:lo:hi i64:lo:hi f32:lo:hi f64:lo:hi
//      buf:<type>:<count>:lo:hi (device buffer, random content, read back after the run)
// prints per set: "set <i> in <hex args> out <hex buffers> agree <n>/<repeats>" and "diff <r> <hex>" per disagreeing repeat

static void check(CUresult r, const char *what) {
  if (r != CUDA_SUCCESS) {
    const char *name = nullptr;
    cuGetErrorName(r, &name);
    std::printf("error %s %s\n", what, name ? name : "?");
    std::fflush(stdout);
    std::_Exit(4);
  }
}

static uint64_t splitmix(uint64_t &s) {
  uint64_t z = (s += 0x9e3779b97f4a7c15ULL);
  z = (z ^ (z >> 30)) * 0xbf58476d1ce4e5b9ULL;
  z = (z ^ (z >> 27)) * 0x94d049bb133111ebULL;
  return z ^ (z >> 31);
}

struct Arg {
  bool buffer;
  std::string type;
  size_t count;
  double lo, hi;
};

static size_t width(const std::string &t) { return t == "u64" || t == "i64" || t == "f64" ? 8 : t == "u16" || t == "i16" ? 2 : t == "u8" ? 1 : 4; }

static void value(const Arg &a, uint64_t &s, uint8_t *out) {
  double u = (double)(splitmix(s) >> 11) / 9007199254740992.0;
  if (a.type == "f32") {
    float f = (float)(a.lo + u * (a.hi - a.lo));
    std::memcpy(out, &f, 4);
  } else if (a.type == "f64") {
    double d = a.lo + u * (a.hi - a.lo);
    std::memcpy(out, &d, 8);
  } else {
    int64_t span = (int64_t)a.hi - (int64_t)a.lo + 1;
    int64_t v = (int64_t)a.lo + (int64_t)(splitmix(s) % (uint64_t)span);
    std::memcpy(out, &v, width(a.type));
  }
}

static std::string hex(const std::vector<uint8_t> &b) {
  static const char *d = "0123456789abcdef";
  std::string s;
  for (uint8_t x : b) s += d[x >> 4], s += d[x & 15];
  return s;
}

static std::vector<std::string> split(const std::string &s, char c) {
  std::vector<std::string> out;
  size_t at = 0, next;
  while ((next = s.find(c, at)) != std::string::npos) out.push_back(s.substr(at, next - at)), at = next + 1;
  out.push_back(s.substr(at));
  return out;
}

int main(int argc, char **argv) {
  if (argc < 8) return std::fprintf(stderr, "usage\n"), 2;
  std::ifstream file(argv[1], std::ios::binary);
  std::vector<char> image((std::istreambuf_iterator<char>(file)), {});
  const char *kernel = argv[2];
  unsigned block = std::atoi(argv[3]), grid = std::atoi(argv[4]);
  int sets = std::atoi(argv[5]), repeats = std::atoi(argv[6]);
  uint64_t seed = std::strtoull(argv[7], nullptr, 0);
  std::vector<Arg> args;
  for (int i = 8; i < argc; i++) {
    auto f = split(argv[i], ':');
    if (f[0] == "buf") args.push_back({true, f[1], std::stoul(f[2]), std::stod(f[3]), std::stod(f[4])});
    else args.push_back({false, f[0], 1, std::stod(f[1]), std::stod(f[2])});
  }
  check(cuInit(0), "init");
  CUdevice device;
  check(cuDeviceGet(&device, 0), "device");
  CUcontext context;
  check(cuDevicePrimaryCtxRetain(&context, device), "context");
  check(cuCtxSetCurrent(context), "current");
  CUmodule module;
  check(cuModuleLoadData(&module, image.data()), "load");
  CUfunction function;
  check(cuModuleGetFunction(&function, module, kernel), "function");
  CUstream stream;
  check(cuStreamCreate(&stream, CU_STREAM_DEFAULT), "stream");
  std::vector<CUdeviceptr> device_buffers(args.size(), 0);
  for (size_t i = 0; i < args.size(); i++)
    if (args[i].buffer) check(cuMemAlloc(&device_buffers[i], args[i].count * width(args[i].type)), "alloc");
  for (int set = 0; set < sets; set++) {
    uint64_t state = seed * 0x100000001b3ULL + set;
    std::vector<std::vector<uint8_t>> initial(args.size());
    for (size_t i = 0; i < args.size(); i++) {
      initial[i].resize(args[i].count * width(args[i].type));
      for (size_t k = 0; k < args[i].count; k++) value(args[i], state, initial[i].data() + k * width(args[i].type));
    }
    std::string first;
    std::vector<std::pair<int, std::string>> differing;
    int agree = 0;
    for (int r = 0; r < repeats; r++) {
      std::vector<void *> params(args.size());
      for (size_t i = 0; i < args.size(); i++) {
        if (args[i].buffer) {
          check(cuMemcpyHtoD(device_buffers[i], initial[i].data(), initial[i].size()), "write");
          params[i] = &device_buffers[i];
        } else params[i] = initial[i].data();
      }
      check(cuCtxSynchronize(), "upload");
      check(cuLaunchKernel(function, grid, 1, 1, block, 1, 1, 0, stream, params.data(), nullptr), "launch");
      auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
      CUresult q;
      while ((q = cuStreamQuery(stream)) == CUDA_ERROR_NOT_READY) {
        if (std::chrono::steady_clock::now() > deadline) {
          std::printf("timeout set %d repeat %d\n", set, r);
          std::fflush(stdout);
          std::_Exit(3);
        }
        std::this_thread::sleep_for(std::chrono::microseconds(50));
      }
      check(q, "run");
      std::string out;
      for (size_t i = 0; i < args.size(); i++)
        if (args[i].buffer) {
          std::vector<uint8_t> b(initial[i].size());
          check(cuMemcpyDtoH(b.data(), device_buffers[i], b.size()), "read");
          if (b.size() > 256) {
            uint64_t h = 0xcbf29ce484222325ULL;
            for (uint8_t x : b) h = (h ^ x) * 0x100000001b3ULL;
            char text[32];
            std::snprintf(text, sizeof text, "h%016llx|", (unsigned long long)h);
            out += text;
          } else out += hex(b) + "|";
        }
      if (r == 0) first = out;
      if (out == first) agree++;
      else if (differing.size() < 4) differing.push_back({r, out});
    }
    std::string in;
    for (auto &b : initial) in += (b.size() > 256 ? std::string("long") : hex(b)) + "|";
    std::printf("set %d in %s out %s agree %d/%d\n", set, in.c_str(), first.c_str(), agree, repeats);
    for (auto &[r, b] : differing) std::printf("diff %d %s\n", r, b.c_str());
  }
  std::fflush(stdout);
  return 0;
}
