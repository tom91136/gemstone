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
//      f32/f64/int scalars take <type>:special (mixed zeros, infinities, NaNs, denormals, boundary exponents) or <type>:edge
//      (exponents only at the format's boundaries); buffers take buf:<type>:<count>:special or :edge
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
  int special;
};

static size_t width(const std::string &t) { return t == "u64" || t == "i64" || t == "f64" ? 8 : t == "u16" || t == "i16" ? 2 : t == "u8" ? 1 : 4; }

static const uint32_t F32_SPECIAL[] = {0x00000000, 0x80000000, 0x7f800000, 0xff800000, 0x7fc00000, 0x7fa00000, 0x00000001, 0x007fffff,
                                       0x00800000, 0x7f7fffff, 0x3f800000, 0xbf800000, 0x40000000, 0x3f000000, 0x00400000, 0x7f000000};
static const uint64_t F64_SPECIAL[] = {0x0000000000000000ULL, 0x8000000000000000ULL, 0x7ff0000000000000ULL, 0xfff0000000000000ULL,
                                       0x7ff8000000000000ULL, 0x0000000000000001ULL, 0x000fffffffffffffULL, 0x0010000000000000ULL,
                                       0x7fefffffffffffffULL, 0x3ff0000000000000ULL, 0xbff0000000000000ULL, 0x4000000000000000ULL};
static const int64_t INT_SPECIAL[] = {0, 1, -1, 2, -2, 3, 7, 8, 100, 255, 65535, 2147483647LL, -2147483648LL, 4294967295LL, 1LL << 62,
                                      -(1LL << 62), INT64_MIN, INT64_MAX};

static void special(const Arg &a, uint64_t &s, uint8_t *out) {
  uint64_t r = splitmix(s), sel = r % 100, bits = splitmix(s);
  if (a.type == "f32") {
    uint32_t b = (uint32_t)bits;
    uint32_t sign = (b >> 31) << 31, mant = b & 0x7fffff;
    if (sel < 25) {
    } else if (sel < 40) b = F32_SPECIAL[(r >> 8) % (sizeof F32_SPECIAL / sizeof *F32_SPECIAL)];
    else if (sel < 55) {
      uint32_t e = (r >> 16) & 1 ? 250 + (r >> 20) % 6 : (r >> 20) % 4;
      b = sign | (e << 23) | mant;
    } else {
      uint32_t e = 127 - 24 + (r >> 20) % 49;
      b = sign | (e << 23) | mant;
    }
    std::memcpy(out, &b, 4);
  } else if (a.type == "f64") {
    uint64_t b = bits;
    uint64_t sign = (b >> 63) << 63, mant = b & 0xfffffffffffffULL;
    if (sel < 25) {
    } else if (sel < 40) b = F64_SPECIAL[(r >> 8) % (sizeof F64_SPECIAL / sizeof *F64_SPECIAL)];
    else if (sel < 55) {
      uint64_t e = (r >> 16) & 1 ? 2040 + (r >> 20) % 8 : (r >> 20) % 8;
      b = sign | (e << 52) | mant;
    } else {
      uint64_t e = 1023 - 60 + (r >> 20) % 121;
      b = sign | (e << 52) | mant;
    }
    std::memcpy(out, &b, 8);
  } else {
    int64_t v;
    if (sel < 25) v = (int64_t)bits;
    else if (sel < 50) v = INT_SPECIAL[(r >> 8) % (sizeof INT_SPECIAL / sizeof *INT_SPECIAL)];
    else {
      int k = (r >> 8) % 33;
      v = (int64_t)(bits & ((1ULL << k) - 1));
      if ((r >> 16) & 1) v = -v;
    }
    std::memcpy(out, &v, width(a.type));
  }
}

static const uint32_t E32[] = {0, 1, 2, 64, 126, 127, 128, 190, 253, 254, 255};
static const uint64_t E64[] = {0, 1, 2, 1000, 1022, 1023, 1024, 2045, 2046, 2047};

static void edge(const Arg &a, uint64_t &s, uint8_t *out) {
  uint64_t r = splitmix(s), bits = splitmix(s);
  if (a.type == "f32") {
    uint32_t m = (r >> 40) % 3 == 0 ? 0 : (uint32_t)bits & 0x7fffff;
    uint32_t b = (uint32_t)((bits >> 40) & 1) << 31 | E32[(r >> 8) % (sizeof E32 / sizeof *E32)] << 23 | m;
    std::memcpy(out, &b, 4);
  } else if (a.type == "f64") {
    uint64_t m = (r >> 40) % 3 == 0 ? 0 : bits & 0xfffffffffffffULL;
    uint64_t b = ((bits >> 63) << 63) | E64[(r >> 8) % (sizeof E64 / sizeof *E64)] << 52 | m;
    std::memcpy(out, &b, 8);
  } else {
    int64_t v = INT_SPECIAL[(r >> 8) % (sizeof INT_SPECIAL / sizeof *INT_SPECIAL)];
    std::memcpy(out, &v, width(a.type));
  }
}

static void value(const Arg &a, uint64_t &s, uint8_t *out) {
  if (a.special == 2) return edge(a, s, out);
  if (a.special) return special(a, s, out);
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
    if (f[0] == "buf") args.push_back({true, f[1], std::stoul(f[2]), 0, 0, f[3] == "special" ? 1 : f[3] == "edge" ? 2 : 0});
    else args.push_back({false, f[0], 1, 0, 0, f[1] == "special" ? 1 : f[1] == "edge" ? 2 : 0});
    Arg &last = args.back();
    if (!last.special) {
      last.lo = std::stod(f[last.buffer ? 3 : 1]);
      last.hi = std::stod(f[last.buffer ? 4 : 2]);
    }
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
