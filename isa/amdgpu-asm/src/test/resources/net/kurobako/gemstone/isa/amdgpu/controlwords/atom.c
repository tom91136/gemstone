__attribute__((amdgpu_kernel)) void atom(int *out, int *counter) {
  int old = __atomic_fetch_add(counter, 1, __ATOMIC_RELAXED);
  int more = __atomic_fetch_add(counter, 2, __ATOMIC_RELAXED);
  out[0] = old + more;
  out[1] = old - more;
}
