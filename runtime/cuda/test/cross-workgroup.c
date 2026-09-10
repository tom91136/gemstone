#include <dlfcn.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Standalone native Driver API client; MUnit supplies the authenticated emulator endpoint. */
static const char source[] =
  ".version 8.0\n"
  ".target sm_80\n"
  ".address_size 64\n"
  ".global .align 4 .u32 metadata_probe[4];\n"
  ".visible .entry compact(\n"
  "    .param .u64 descriptors, .param .u64 output, .param .u32 count) {\n"
  "    .reg .pred %p<4>;\n"
  "    .reg .u32 %r<12>;\n"
  "    .reg .u64 %rd<7>;\n"
  "    mov.u32 %r0, %tid.x;\n"
  "    setp.ne.u32 %p0, %r0, 0;\n"
  "    @%p0 ret;\n"
  "    ld.param.u64 %rd0, [descriptors];\n"
  "    ld.param.u64 %rd1, [output];\n"
  "    ld.param.u32 %r1, [count];\n"
  "    mov.u32 %r2, %ctaid.x;\n"
  "    mov.u32 %r3, %ntid.x;\n"
  "    mul.lo.u32 %r4, %r2, %r3;\n"
  "    add.u32 %r5, %r4, %r3;\n"
  "    min.u32 %r5, %r5, %r1;\n"
  "    mul.wide.u32 %rd2, %r2, 8;\n"
  "    add.u64 %rd2, %rd2, %rd0;\n"
  "    mov.u32 %r6, 0;\n"
  "    setp.eq.u32 %p1, %r2, 0;\n"
  "    @%p1 bra select;\n"
  "  predecessor:\n"
  "    ld.acquire.gpu.v2.u32 {%r7, %r6}, [%rd2-8];\n"
  "    setp.ne.u32 %p1, %r7, 101;\n"
  "    @%p1 bra predecessor;\n"
  "  select:\n"
  "    setp.ge.u32 %p2, %r4, %r5;\n"
  "    @%p2 bra publish;\n"
  "    and.b32 %r8, %r4, 1;\n"
  "    setp.ne.u32 %p3, %r8, 0;\n"
  "    @%p3 bra next;\n"
  "    mul.wide.u32 %rd3, %r6, 4;\n"
  "    add.u64 %rd3, %rd3, %rd1;\n"
  "    st.global.u32 [%rd3], %r4;\n"
  "    add.u32 %r6, %r6, 1;\n"
  "  next:\n"
  "    add.u32 %r4, %r4, 1;\n"
  "    bra select;\n"
  "  publish:\n"
  "    mov.u32 %r7, 101;\n"
  "    st.release.gpu.v2.u32 [%rd2], {%r7, %r6};\n"
  "    ret;\n"
  "  }\n"
  ".visible .entry count(.param .u64 output, .param .u32 size, .param .u32 epoch) {\n"
  ".reg .pred %p; .reg .u32 %r<7>; .reg .u64 %rd<3>;\n"
  "ld.param.u64 %rd0, [output]; ld.param.u32 %r0, [size]; ld.param.u32 %r6, [epoch];\n"
  "mov.u32 %r1, %ctaid.x; mov.u32 %r2, %ntid.x; mov.u32 %r3, %tid.x;\n"
  "mad.lo.u32 %r4, %r1, %r2, %r3;\n"
  "setp.ge.u32 %p, %r4, %r0; @%p ret;\n"
  "atom.global.add.u32 %r5, [%rd0], 1;\n"
  "mul.wide.u32 %rd1, %r4, 4; add.u64 %rd1, %rd1, %rd0;\n"
  "add.u32 %r4, %r4, %r6; st.global.u32 [%rd1+8], %r4;\n"
  "ret;\n"
  "}\n"
  "\n"
  ".visible .entry publish(.param .u64 memory) {\n"
  "    .reg .pred %p<2>;\n"
  "    .reg .u32 %r<3>;\n"
  "    .reg .u64 %rd<7>;\n"
  "    mov.u32 %r0, %tid.x;\n"
  "    setp.ne.u32 %p0, %r0, 0;\n"
  "    @%p0 ret;\n"
  "    ld.param.u64 %rd0, [memory];\n"
  "    mov.u32 %r1, %ctaid.x;\n"
  "    setp.ne.u32 %p0, %r1, 0;\n"
  "    @%p0 bra observe;\n"
  "    mov.u64 %rd1, 0x1122334455667788;\n"
  "    mov.u64 %rd2, 0x1020304050607080;\n"
  "    st.global.v2.u64 [%rd0], {%rd1, %rd2};\n"
  "    mov.u32 %r2, 1;\n"
  "    st.release.gpu.u32 [%rd0+16], %r2;\n"
  "  observe:\n"
  "    ld.acquire.gpu.u32 %r2, [%rd0+16];\n"
  "    setp.eq.u32 %p1, %r2, 0;\n"
  "    @%p1 bra observe;\n"
  "    ld.global.v2.u64 {%rd3, %rd4}, [%rd0];\n"
  "    mul.wide.u32 %rd6, %r1, 16;\n"
  "    add.u64 %rd6, %rd6, %rd0;\n"
  "    st.global.u64 [%rd6+24], %rd3;\n"
  "    st.global.u64 [%rd6+32], %rd4;\n"
  "    ret;\n"
  "  }\n";

#define LOAD(name, signature) typedef int (*name##_fn) signature; name##_fn name = (name##_fn)dlsym(library, #name); if (!name) { fprintf(stderr, "missing %s\n", #name); return 1; }
#define CHECK(call) do { int status = (call); if (status) { fprintf(stderr, "%s: CUDA status %d\n", #call, status); return 1; } } while (0)
#define REQUIRE(condition) do { if (!(condition)) { fprintf(stderr, "line %d: %s\n", __LINE__, #condition); return 1; } } while (0)

int main(void) {
  const char *path = getenv("GEMSTONE_CUDA_TEST_LIBRARY");
  void *library = dlopen(path ? path : "libcuda.so.1", RTLD_NOW | RTLD_LOCAL);
  if (!library) { fprintf(stderr, "%s\n", dlerror()); return 1; }
  LOAD(cuInit, (unsigned));
  LOAD(cuCtxCreate_v2, (void **, unsigned, int));
  LOAD(cuCtxDestroy_v2, (void *));
  LOAD(cuCtxSetCurrent, (void *));
  LOAD(cuModuleLoadData, (void **, const void *));
  LOAD(cuModuleGetFunction, (void **, void *, const char *));
  LOAD(cuModuleUnload, (void *));
  LOAD(cuModuleGetGlobal_v2, (uint64_t *, size_t *, void *, const char *));
  LOAD(cuMemAlloc_v2, (uint64_t *, size_t));
  LOAD(cuMemFree_v2, (uint64_t));
  LOAD(cuPointerGetAttribute, (void *, int32_t, uint64_t));
  LOAD(cuPointerGetAttributes, (uint32_t, int32_t *, void **, uint64_t));
  LOAD(cuMemAllocManaged, (uint64_t *, size_t, uint32_t));
  LOAD(cuMemHostAlloc, (void **, size_t, uint32_t));
  LOAD(cuMemFreeHost, (void *));
  LOAD(cuMemcpyHtoD_v2, (uint64_t, const void *, size_t));
  LOAD(cuMemcpyDtoH_v2, (void *, uint64_t, size_t));
  LOAD(cuMemcpy, (uint64_t, uint64_t, size_t));
  LOAD(cuMemcpyAsync, (uint64_t, uint64_t, size_t, void *));
  LOAD(cuLaunchKernel, (void *, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, void *, void **, void **));
  CHECK(cuInit(0));
  void *context = NULL, *module = NULL, *compact = NULL, *count = NULL, *publish = NULL;
  CHECK(cuCtxCreate_v2(&context, 0, 0));
  CHECK(cuModuleLoadData(&module, source));
  CHECK(cuModuleGetFunction(&compact, module, "compact"));
  CHECK(cuModuleGetFunction(&count, module, "count"));
  CHECK(cuModuleGetFunction(&publish, module, "publish"));
  uint64_t memory = 0;
  uint32_t host[1024];
  CHECK(cuMemAlloc_v2(&memory, sizeof(host)));
  uint32_t memory_type = 99, managed = 99;
  uint64_t pointer_value = 0, range_size = 0, buffer_id = 0;
  void *ordinary_heap = malloc(64);
  REQUIRE(ordinary_heap != NULL);
  const uint64_t unregistered[] = {(uint64_t)(uintptr_t)host, (uint64_t)(uintptr_t)ordinary_heap, 0};
  for (unsigned i = 0; i < 3; ++i) {
    REQUIRE(cuPointerGetAttribute(&memory_type, 2, unregistered[i]) == 1);
    int32_t attributes[] = {2, 3, 8};
    void *values[] = {&memory_type, &pointer_value, &managed};
    memory_type = managed = 99; pointer_value = 99;
    CHECK(cuPointerGetAttributes(3, attributes, values, unregistered[i]));
    REQUIRE(memory_type == 0 && pointer_value == 0 && managed == 0);
  }
  free(ordinary_heap);
  CHECK(cuPointerGetAttribute(&memory_type, 2, memory + 4)); REQUIRE(memory_type == 2);
  CHECK(cuPointerGetAttribute(&pointer_value, 11, memory + 4)); REQUIRE(pointer_value == memory);
  CHECK(cuPointerGetAttribute(&range_size, 12, memory + 4)); REQUIRE(range_size == sizeof(host));
  CHECK(cuPointerGetAttribute(&buffer_id, 7, memory)); REQUIRE(buffer_id != 0);
  REQUIRE(cuPointerGetAttribute(&pointer_value, 4, memory) == 1);
  uint32_t sample = 0x12345678, copied = 0;
  CHECK(cuMemcpy(memory + 4, (uint64_t)(uintptr_t)&sample, sizeof(sample)));
  CHECK(cuMemcpyAsync((uint64_t)(uintptr_t)&copied, memory + 4, sizeof(copied), NULL));
  REQUIRE(copied == sample);
  uint64_t unified = 0;
  CHECK(cuMemAllocManaged(&unified, 64, 1));
  CHECK(cuPointerGetAttribute(&managed, 8, unified + 8)); REQUIRE(managed == 1);
  CHECK(cuPointerGetAttribute(&pointer_value, 4, unified + 8)); REQUIRE(pointer_value == unified + 8);
  copied = 0;
  CHECK(cuMemcpy(unified + 8, (uint64_t)(uintptr_t)&sample, sizeof(sample)));
  CHECK(cuMemcpy((uint64_t)(uintptr_t)&copied, unified + 8, sizeof(copied)));
  REQUIRE(copied == sample);
  CHECK(cuMemFree_v2(unified));
  REQUIRE(cuPointerGetAttribute(&managed, 8, unified) == 1);
  void *pinned = NULL;
  CHECK(cuMemHostAlloc(&pinned, 64, 0));
  CHECK(cuPointerGetAttribute(&memory_type, 2, (uint64_t)(uintptr_t)pinned)); REQUIRE(memory_type == 1);
  CHECK(cuPointerGetAttribute(&managed, 8, (uint64_t)(uintptr_t)pinned)); REQUIRE(managed == 0);
  CHECK(cuMemFreeHost(pinned));
  REQUIRE(cuPointerGetAttribute(&memory_type, 2, (uint64_t)(uintptr_t)pinned) == 1);
  void *allocation_context = NULL, *other_context = NULL;
  CHECK(cuCtxCreate_v2(&other_context, 0, 0));
  REQUIRE(other_context != context);
  uint64_t other_memory = 0;
  CHECK(cuMemAlloc_v2(&other_memory, sizeof(uint32_t)));
  const uint32_t other_value = 99;
  CHECK(cuMemcpyHtoD_v2(other_memory, &other_value, sizeof(other_value)));
  CHECK(cuPointerGetAttribute(&allocation_context, 1, memory)); REQUIRE(allocation_context == context);
  CHECK(cuCtxDestroy_v2(other_context));
  REQUIRE(cuMemcpyHtoD_v2(other_memory, &other_value, sizeof(other_value)) != 0);
  CHECK(cuCtxSetCurrent(NULL));
  REQUIRE(cuPointerGetAttribute(&pointer_value, 3, memory) == 201);
  CHECK(cuPointerGetAttribute(&allocation_context, 1, memory)); REQUIRE(allocation_context == context);
  CHECK(cuCtxSetCurrent(context));
  uint64_t global = 0;
  size_t global_size = 0;
  CHECK(cuModuleGetGlobal_v2(&global, &global_size, module, "metadata_probe"));
  REQUIRE(global_size == 16);
  CHECK(cuPointerGetAttribute(&range_size, 12, global + 4)); REQUIRE(range_size == 16);
  const unsigned groups[] = {1, 2, 3, 5, 9};
  const unsigned widths[] = {31, 32, 33, 64, 65};
  unsigned launches = 0;
  for (unsigned g = 0; g < sizeof(groups) / sizeof(groups[0]); ++g) {
    for (unsigned w = 0; w < sizeof(widths) / sizeof(widths[0]); ++w) {
      unsigned n = groups[g] * widths[w] - widths[w] / 2;
      for (unsigned repeat = 0; repeat < 2; ++repeat) {
        memset(host, 0, sizeof(host));
        CHECK(cuMemcpyHtoD_v2(memory, host, sizeof(host)));
        void *publication_arguments[] = {&memory};
        CHECK(cuLaunchKernel(publish, groups[g], 1, 1, widths[w], 1, 1, 0, NULL, publication_arguments, NULL));
        ++launches;
        CHECK(cuMemcpyDtoH_v2(host, memory, sizeof(host)));
        for (unsigned block = 0; block < groups[g]; ++block) {
          uint64_t first = host[6 + block * 4] | ((uint64_t)host[7 + block * 4] << 32);
          uint64_t second = host[8 + block * 4] | ((uint64_t)host[9 + block * 4] << 32);
          REQUIRE(first == UINT64_C(0x1122334455667788));
          REQUIRE(second == UINT64_C(0x1020304050607080));
        }
        memset(host, 0, sizeof(host));
        CHECK(cuMemcpyHtoD_v2(memory, host, sizeof(host)));
        uint64_t output = memory + groups[g] * 8;
        void *arguments[] = {&memory, &output, &n};
        CHECK(cuLaunchKernel(compact, groups[g], 1, 1, widths[w], 1, 1, 0, NULL, arguments, NULL));
        ++launches;
        CHECK(cuMemcpyDtoH_v2(host, memory, sizeof(host)));
        REQUIRE(host[2 * groups[g] - 1] == (n + 1) / 2);
        for (unsigned block = 0; block < groups[g]; ++block) {
          unsigned end = (block + 1) * widths[w];
          if (end > n) end = n;
          REQUIRE(host[2 * block] == 101);
          REQUIRE(host[2 * block + 1] == (end + 1) / 2);
        }
        for (unsigned i = 0; i < (n + 1) / 2; ++i) REQUIRE(host[2 * groups[g] + i] == 2 * i);
      }
      memset(host, 0, sizeof(host));
      CHECK(cuMemcpyHtoD_v2(memory, host, sizeof(host)));
      for (unsigned repeat = 1; repeat <= 2; ++repeat) {
        unsigned epoch = repeat * 1000;
        void *arguments[] = {&memory, &n, &epoch};
        CHECK(cuLaunchKernel(count, groups[g], 1, 1, widths[w], 1, 1, 0, NULL, arguments, NULL));
        ++launches;
        CHECK(cuMemcpyDtoH_v2(host, memory, sizeof(host)));
        REQUIRE(host[0] == repeat * n);
        for (unsigned i = 0; i < n; ++i) REQUIRE(host[2 + i] == epoch + i);
      }
    }
  }
  CHECK(cuMemFree_v2(memory));
  REQUIRE(cuPointerGetAttribute(&memory_type, 2, memory) == 1);
  CHECK(cuModuleUnload(module));
  REQUIRE(cuPointerGetAttribute(&memory_type, 2, global) == 1);
  CHECK(cuMemAlloc_v2(&memory, 32));
  uint64_t new_id = 0;
  CHECK(cuPointerGetAttribute(&new_id, 7, memory)); REQUIRE(new_id != buffer_id);
  CHECK(cuCtxDestroy_v2(context));
  REQUIRE(cuPointerGetAttribute(&memory_type, 2, memory) == 1);
  printf("CUDA cross-workgroup: %u checked launches, groups 1/2/3/5/9, lanes 31/32/33/64/65\n", launches);
  dlclose(library);
  return 0;
}
