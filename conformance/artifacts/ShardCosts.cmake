# What a route costs a CI run shard, in case seconds: the sum of the wall time of its cases, as the emulated run
# measures it at the two concurrent cases of a 4-core runner. Each value is the worst of the recorded samples, so a
# cold runner does not outrun its shard. A shard of n cases in flight takes cost / n seconds.
#
# A program has an entry here when its cases take long enough to size a shard: the interpreter programs and the HeCBench
# programs of hecbench-bounded.toml. Every other case of a route is the base cost of its backend.
#
# conformance_memory_<program> is the resident memory in megabytes of one running case of a program, summed over its process
# tree, as the CI memory samples of a cuda shard show it. The emulated run admits cases while their needs fit the memory the
# server and the reserve leave; a program without an entry takes an even share. A Julia package test is a driver and a worker
# process, and the CUDA.jl figure is the larger of two cases sampled together (2863 + 1927 MB); AMDGPU.jl and oneAPI.jl run
# the same package test runner and take it unmeasured.
set(conformance_shard_jobs 2)
set(conformance_shard_wall_seconds 14400)

set(conformance_cost_programs
  python_numba julia_babelstream_cuda julia_tests_cuda julia_babelstream_amdgpu julia_tests_amdgpu
  julia_babelstream_oneapi julia_tests_oneapi hecbench_abacus hecbench_bitpacking hecbench_fsm hecbench_laplace
  hecbench_minimod hecbench_overlap hecbench_sdma hecbench_streamcluster)
set(conformance_cost_python_numba 10824)
set(conformance_cost_julia_babelstream_cuda 24)
set(conformance_cost_julia_tests_cuda 17134)
set(conformance_cost_julia_babelstream_amdgpu 62)
set(conformance_cost_julia_tests_amdgpu 7688)
set(conformance_cost_julia_babelstream_oneapi 25)
set(conformance_cost_julia_tests_oneapi 7889)
set(conformance_cost_hecbench_abacus 9)
set(conformance_cost_hecbench_bitpacking 2341)
set(conformance_cost_hecbench_fsm 89)
set(conformance_cost_hecbench_laplace 347)
set(conformance_cost_hecbench_minimod 29)
set(conformance_cost_hecbench_overlap 181)
set(conformance_cost_hecbench_sdma 103)
set(conformance_cost_hecbench_streamcluster 202)

set(conformance_memory_python_numba 2048)
set(conformance_memory_julia_tests_cuda 4800)
set(conformance_memory_julia_tests_amdgpu 4800)
set(conformance_memory_julia_tests_oneapi 4800)

set(conformance_cost_base_cuda 1766)
set(conformance_cost_base_openacc 111)
set(conformance_cost_base_hip 1525)
set(conformance_cost_base_hsa 117)
set(conformance_cost_base_levelzero 11809)
set(conformance_cost_base_sycl 3245)
