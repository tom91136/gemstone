find_program(sycl_compiler NAMES icpx REQUIRED)
if(NOT "${CMAKE_CXX_COMPILER}" STREQUAL "${sycl_compiler}")
  message(FATAL_ERROR "SYCL vendor tests must use the project C++ compiler: ${CMAKE_CXX_COMPILER}")
endif()

FetchContent_MakeAvailable(oneapi_samples onedpl)
install(FILES "${oneapi_samples_SOURCE_DIR}/License.txt"
  DESTINATION licences RENAME oneapi-samples.txt COMPONENT support)
install(FILES "${onedpl_SOURCE_DIR}/LICENSE.txt"
  DESTINATION licences RENAME oneDPL.txt COMPONENT support)

function(conformance_sycl_program target sources timeout)
  add_executable(${target} ${sources})
  conformance_project_target(${target})
  target_compile_options(${target} PRIVATE -fsycl)
  target_link_options(${target} PRIVATE -fsycl)
  target_include_directories(${target} SYSTEM PRIVATE ${ARGN})
  target_link_libraries(${target} PRIVATE Catch2::Catch2WithMain)
  conformance_register_test_program(${target} ${timeout})
  set(conformance_vendor_targets ${conformance_vendor_targets} ${target} PARENT_SCOPE)
endfunction()

function(conformance_sycl_sample target wrapper upstream timeout)
  set_source_files_properties("${upstream}" PROPERTIES COMPILE_DEFINITIONS main=conformanceSampleMain COMPILE_OPTIONS -Wno-error)
  conformance_sycl_program(${target} "${wrapper};${upstream}" ${timeout} ${ARGN})
  set(conformance_vendor_targets ${conformance_vendor_targets} PARENT_SCOPE)
endfunction()

function(conformance_sycl_onemkl target)
  target_compile_options(${target} PRIVATE -qmkl=parallel)
  target_link_options(${target} PRIVATE -qmkl=parallel)
endfunction()

set(sycl_samples "${oneapi_samples_SOURCE_DIR}/DirectProgramming/C++SYCL")
set(sycl_samples_common "${oneapi_samples_SOURCE_DIR}/common")
set(sycl_wrappers "${CMAKE_CURRENT_SOURCE_DIR}/sycl/samples")

conformance_sycl_sample(vendor_sycl_sample_vector_add_buffers "${sycl_wrappers}/vector_add_buffers.cpp"
  "${sycl_samples}/DenseLinearAlgebra/vector-add/src/vector-add-buffers.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/DenseLinearAlgebra/vector-add/src" "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_vector_add_usm "${sycl_wrappers}/vector_add_usm.cpp"
  "${sycl_samples}/DenseLinearAlgebra/vector-add/src/vector-add-usm.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/DenseLinearAlgebra/vector-add/src" "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_simple_add_buffers "${sycl_wrappers}/simple_add_buffers.cpp"
  "${sycl_samples}/DenseLinearAlgebra/simple-add/src/simple-add-buffers.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/DenseLinearAlgebra/simple-add/src" "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_simple_add_usm "${sycl_wrappers}/simple_add_usm.cpp"
  "${sycl_samples}/DenseLinearAlgebra/simple-add/src/simple-add-usm.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/DenseLinearAlgebra/simple-add/src" "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_matrix_mul "${sycl_wrappers}/matrix_mul_sycl_sample.cpp"
  "${sycl_samples}/DenseLinearAlgebra/matrix_mul/src/matrix_mul_sycl.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/DenseLinearAlgebra/matrix_mul/src" "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_prefix_sum "${sycl_wrappers}/prefix_sum.cpp"
  "${sycl_samples}/ParallelPatterns/PrefixSum/src/PrefixSum.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/ParallelPatterns/PrefixSum/src" "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_bitonic_sort "${sycl_wrappers}/bitonic_sort.cpp"
  "${sycl_samples}/GraphTraversal/bitonic-sort/src/bitonic-sort.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/GraphTraversal/bitonic-sort/src" "${sycl_samples_common}")
set(sycl_subgroup_reduce "${CMAKE_CURRENT_BINARY_DIR}/sycl_samples/sum_subgroup_reduce")
stage_source("${sycl_samples}/Jupyter/oneapi-essentials-training/08_SYCL_Reduction/src" "${sycl_subgroup_reduce}.staging")
patch_exact("${sycl_subgroup_reduce}.staging/sum_subgroup_reduce.cpp" "  q.single_task(" "  q.wait();\n  q.single_task(")
patch_exact("${sycl_subgroup_reduce}.staging/sum_subgroup_reduce.cpp" "  std::cout << \"Sum = \""
  "  q.wait();\n  std::cout << \"Sum = \"")
commit_staged_source("${sycl_subgroup_reduce}.staging" "${sycl_subgroup_reduce}")
conformance_sycl_sample(vendor_sycl_sample_sum_subgroup_reduce "${sycl_wrappers}/sum_subgroup_reduce_sample.cpp"
  "${sycl_subgroup_reduce}/sum_subgroup_reduce.cpp" ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples_common}")
conformance_sycl_sample(vendor_sycl_sample_reduction_atomics_buffer "${sycl_wrappers}/reduction_atomics_buffer_sample.cpp"
  "${sycl_samples}/Jupyter/oneapi-essentials-training/12_SYCL_Local_Memory_And_Atomics/src/reduction_atomics_buffer.cpp"
  ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples}/Jupyter/oneapi-essentials-training/12_SYCL_Local_Memory_And_Atomics/src"
  "${sycl_samples_common}")

set(sycl_onemkl_samples "${oneapi_samples_SOURCE_DIR}/Libraries/oneMKL")
conformance_sycl_sample(vendor_sycl_sample_matrix_mul_mkl "${sycl_wrappers}/matrix_mul_mkl.cpp"
  "${sycl_onemkl_samples}/matrix_mul_mkl/matrix_mul_mkl.cpp" ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples_common}")
conformance_sycl_onemkl(vendor_sycl_sample_matrix_mul_mkl)
conformance_sycl_sample(vendor_sycl_sample_fcorr_1d_usm "${sycl_wrappers}/fcorr_1d_usm.cpp"
  "${sycl_onemkl_samples}/fourier_correlation/fcorr_1d_usm.cpp" ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${sycl_samples_common}")
conformance_sycl_onemkl(vendor_sycl_sample_fcorr_1d_usm)

file(GLOB sycl_onedpl_sources CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/sycl/onedpl/*.cpp")
foreach(source IN LISTS sycl_onedpl_sources)
  get_filename_component(name "${source}" NAME_WE)
  conformance_sycl_program(vendor_sycl_onedpl_${name} "${source}" ${CONFORMANCE_TIMEOUT_SYCL_FULL} "${onedpl_SOURCE_DIR}/include")
endforeach()
