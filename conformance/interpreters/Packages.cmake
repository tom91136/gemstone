# Interpreter-hosted packages. An environment is keyed by its lock file and shared by every route with a matching
# backend; a package contributes one `cases` test program per entry in its program list.
set(conformance_interpreter_packages numba julia-cuda julia-amdgpu julia-oneapi)

set(conformance_interpreter_package_numba_language python)
set(conformance_interpreter_package_numba_backends cuda)
set(conformance_interpreter_package_numba_environment python/requirements-numba.txt)
set(conformance_interpreter_package_numba_python_directory "cpython-${conformance_python_release}")
set(conformance_interpreter_package_numba_programs python_numba)

set(conformance_interpreter_package_julia-cuda_language julia)
set(conformance_interpreter_package_julia-cuda_backends cuda)
set(conformance_interpreter_package_julia-cuda_environment julia/cuda)
set(conformance_interpreter_package_julia-cuda_programs julia_babelstream_cuda julia_tests_cuda)

set(conformance_interpreter_package_julia-amdgpu_language julia)
set(conformance_interpreter_package_julia-amdgpu_backends hip hsa)
set(conformance_interpreter_package_julia-amdgpu_environment julia/amdgpu)
set(conformance_interpreter_package_julia-amdgpu_programs julia_babelstream_amdgpu julia_tests_amdgpu)

set(conformance_interpreter_package_julia-oneapi_language julia)
set(conformance_interpreter_package_julia-oneapi_backends sycl levelzero)
set(conformance_interpreter_package_julia-oneapi_environment julia/oneapi)
set(conformance_interpreter_package_julia-oneapi_programs julia_babelstream_oneapi julia_tests_oneapi)

set(conformance_interpreter_program_python_numba_script python/numba_cases.py)
set(conformance_interpreter_program_python_numba_timeout 900)

set(conformance_interpreter_program_julia_babelstream_cuda_script julia/babelstream.jl)
set(conformance_interpreter_program_julia_babelstream_cuda_project babelstream)
set(conformance_interpreter_program_julia_babelstream_cuda_arguments cuda)
set(conformance_interpreter_program_julia_babelstream_cuda_timeout 900)
set(conformance_interpreter_program_julia_tests_cuda_script julia/package_tests.jl)
set(conformance_interpreter_program_julia_tests_cuda_project tests)
set(conformance_interpreter_program_julia_tests_cuda_arguments CUDA)
set(conformance_interpreter_program_julia_tests_cuda_timeout 1800)

set(conformance_interpreter_program_julia_babelstream_amdgpu_script julia/babelstream.jl)
set(conformance_interpreter_program_julia_babelstream_amdgpu_project babelstream)
set(conformance_interpreter_program_julia_babelstream_amdgpu_arguments amdgpu)
set(conformance_interpreter_program_julia_babelstream_amdgpu_timeout 900)
set(conformance_interpreter_program_julia_tests_amdgpu_script julia/package_tests.jl)
set(conformance_interpreter_program_julia_tests_amdgpu_project tests)
set(conformance_interpreter_program_julia_tests_amdgpu_arguments AMDGPU)
set(conformance_interpreter_program_julia_tests_amdgpu_timeout 1800)

set(conformance_interpreter_program_julia_babelstream_oneapi_script julia/babelstream.jl)
set(conformance_interpreter_program_julia_babelstream_oneapi_project babelstream)
set(conformance_interpreter_program_julia_babelstream_oneapi_arguments oneapi)
set(conformance_interpreter_program_julia_babelstream_oneapi_timeout 900)
set(conformance_interpreter_program_julia_tests_oneapi_script julia/package_tests.jl)
set(conformance_interpreter_program_julia_tests_oneapi_project tests)
set(conformance_interpreter_program_julia_tests_oneapi_arguments oneAPI)
set(conformance_interpreter_program_julia_tests_oneapi_timeout 1800)

# Programs are listed in the order their cases run: an inventory first, then the benchmark, then package suites.
set(conformance_interpreter_programs
  python_numba
  julia_babelstream_cuda julia_tests_cuda
  julia_babelstream_amdgpu julia_tests_amdgpu
  julia_babelstream_oneapi julia_tests_oneapi)

function(conformance_interpreter_package_of_program program output)
  foreach(package IN LISTS conformance_interpreter_packages)
    if(program IN_LIST conformance_interpreter_package_${package}_programs)
      set(${output} "${package}" PARENT_SCOPE)
      return()
    endif()
  endforeach()
  message(FATAL_ERROR "Unknown interpreter program: ${program}")
endfunction()

function(conformance_interpreter_packages_for_backend backend output)
  set(packages)
  foreach(package IN LISTS conformance_interpreter_packages)
    if(backend IN_LIST conformance_interpreter_package_${package}_backends)
      list(APPEND packages "${package}")
    endif()
  endforeach()
  set(${output} "${packages}" PARENT_SCOPE)
endfunction()

function(conformance_interpreter_programs_for_backend backend output)
  conformance_interpreter_packages_for_backend("${backend}" packages)
  set(programs)
  foreach(package IN LISTS packages)
    list(APPEND programs ${conformance_interpreter_package_${package}_programs})
  endforeach()
  set(${output} "${programs}" PARENT_SCOPE)
endfunction()
