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
set(conformance_cost_python_numba 5700)
set(conformance_cost_julia_babelstream_cuda 10)
set(conformance_cost_julia_tests_cuda 4340)
set(conformance_cost_julia_babelstream_amdgpu 8)
set(conformance_cost_julia_tests_amdgpu 3900)
set(conformance_cost_julia_babelstream_oneapi 5)
set(conformance_cost_julia_tests_oneapi 5400)
set(conformance_cost_hecbench_bitpacking 261)
set(conformance_cost_hecbench_laplace 233)
set(conformance_cost_hecbench_minimod 16)
set(conformance_cost_hecbench_overlap 56)

set(conformance_cost_base_cuda 1200)
set(conformance_cost_base_openacc 200)
set(conformance_cost_base_hip 50)
set(conformance_cost_base_hsa 50)
set(conformance_cost_base_levelzero 10600)
set(conformance_cost_base_sycl 2100)
