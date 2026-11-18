#define LOOP(body)                                                     \
  __asm__ volatile("s_mov_b32 s17, 0x400\n"                            \
                   ".p2align 6\n"                                      \
                   "1:\n" body "s_add_i32 s17, s17, -1\n"              \
                   "s_cmp_lg_u32 s17, 0\n"                             \
                   "s_cbranch_scc1 1b\n"                               \
                   :                                                   \
                   :                                                   \
                   : "v3", "v4", "v5", "v6", "v7", "s17", "scc")

#define FMA4                   \
  "v_fma_f32 v6, v6, s5, s6\n" \
  "v_fma_f32 v5, v5, s5, s6\n" \
  "v_fma_f32 v4, v4, s5, s6\n" \
  "v_fma_f32 v3, v3, s5, s6\n"

__attribute__((amdgpu_kernel)) void ctl_f4_tail(void) { LOOP(FMA4); }

__attribute__((amdgpu_kernel)) void ctl_f4_dead(void) { LOOP(FMA4 "v_mov_b32 v7, v5\n"); }

__attribute__((amdgpu_kernel)) void square_plus_one(const float *restrict in, float *restrict out, unsigned n) {
  for (unsigned i = 0; i < n; ++i) out[i] = in[i] * in[i] + 1.0f;
}

__attribute__((amdgpu_kernel)) void window(void) {}
