# What a route costs a CI run shard, in case seconds: the sum of the wall time of its cases, as the emulated run
# measures it at the two concurrent cases of a 4-core runner. Each value is the worst of the recorded samples, so a
# cold runner does not outrun its shard. A shard of n cases in flight takes cost / n seconds.
#
# A program has an entry here when its cases take long enough to size a shard: the interpreter programs and the HeCBench
# programs of hecbench-bounded.toml. Every other case of a route is the base cost of its backend.
set(conformance_shard_jobs 2)
set(conformance_shard_wall_seconds 14400)

set(conformance_cost_programs
  python_numba julia_babelstream_cuda julia_tests_cuda julia_babelstream_amdgpu julia_tests_amdgpu
  julia_babelstream_oneapi julia_tests_oneapi hecbench_bitpacking hecbench_laplace hecbench_minimod hecbench_overlap)
set(conformance_cost_python_numba 10824)
set(conformance_cost_julia_babelstream_cuda 24)
set(conformance_cost_julia_tests_cuda 17134)
set(conformance_cost_julia_babelstream_amdgpu 62)
set(conformance_cost_julia_tests_amdgpu 7688)
set(conformance_cost_julia_babelstream_oneapi 25)
set(conformance_cost_julia_tests_oneapi 7889)
set(conformance_cost_hecbench_bitpacking 2341)
set(conformance_cost_hecbench_laplace 347)
set(conformance_cost_hecbench_minimod 29)
set(conformance_cost_hecbench_overlap 181)

set(conformance_cost_base_cuda 1766)
set(conformance_cost_base_openacc 111)
set(conformance_cost_base_hip 1525)
set(conformance_cost_base_hsa 117)
set(conformance_cost_base_levelzero 11809)
set(conformance_cost_base_sycl 3245)
