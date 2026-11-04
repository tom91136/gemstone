set(vendor_cccl_includes
  "${vendor_cccl_root}/thrust" "${vendor_cccl_root}/cub" "${vendor_cccl_root}/libcudacxx/include")
if(EXISTS "${vendor_cccl_root}/cudax/include")
  list(APPEND vendor_cccl_includes "${vendor_cccl_root}/cudax/include")
endif()

function(conformance_vendor_cuda_cccl_case family id relative_source)
  cmake_parse_arguments(PARSE_ARGV 3 CASE "" "ROOT" "INCLUDES;OPTIONS;ENTRY_INCLUDES")
  if(NOT CASE_ROOT)
    set(CASE_ROOT "${vendor_cccl_root}")
  endif()
  set(source "${CASE_ROOT}/${relative_source}")
  if(NOT EXISTS "${source}")
    message(FATAL_ERROR "Missing CCCL source: ${source}")
  endif()
  set_source_files_properties("${source}" PROPERTIES LANGUAGE CUDA)
  get_filename_component(source_directory "${source}" DIRECTORY)
  conformance_vendor_cuda_program(vendor_cuda_cccl_${family}_${id}
    TAG "[vendor][cccl][${family}]${vendor_release_tag}"
    CASES "${family}_${id}|vendor/cccl/${family}/${id}|${source}"
    INCLUDES ${vendor_cccl_includes} "${source_directory}" ${CASE_INCLUDES}
    ENTRY_INCLUDES ${CASE_ENTRY_INCLUDES}
    DEFINITIONS LIBCUDACXX_IGNORE_DEPRECATED_ABI
    OPTIONS ${CASE_OPTIONS} -UNDEBUG --extended-lambda --expt-relaxed-constexpr)
  set(conformance_vendor_targets "${conformance_vendor_targets}" PARENT_SCOPE)
endfunction()

set(vendor_thrust_examples "${vendor_programs_directory}/cccl_thrust_examples")
stage_source("${vendor_cccl_root}/thrust/examples" "${vendor_thrust_examples}.staging")
patch_exact("${vendor_thrust_examples}.staging/sum.cu"
  "  // print the sum"
  "  if (sum != thrust::reduce(h_vec.begin(), h_vec.end(), 0)) return 1;\n  // print the sum")
patch_exact("${vendor_thrust_examples}.staging/norm.cu"
  "  std::cout << \"norm is \""
  "  if (std::fabs(norm - std::sqrt(30.0f)) > 1e-4f) return 1;\n  std::cout << \"norm is \"")
patch_exact("${vendor_thrust_examples}.staging/saxpy.cu"
  "    saxpy_slow(2.0, X, Y);"
  "    saxpy_slow(2.0, X, Y);\n    if (thrust::host_vector<float>(Y) != thrust::host_vector<float>{3.0f, 4.0f, 5.0f, 6.0f}) return 1;")
patch_exact("${vendor_thrust_examples}.staging/saxpy.cu"
  "    saxpy_fast(2.0, X, Y);"
  "    saxpy_fast(2.0, X, Y);\n    if (thrust::host_vector<float>(Y) != thrust::host_vector<float>{3.0f, 4.0f, 5.0f, 6.0f}) return 1;")
patch_exact("${vendor_thrust_examples}.staging/dot_products_with_zip.cu"
  "  std::cout << std::fixed;"
  "  thrust::host_vector<float> a0(A0), a1(A1), a2(A2), b0(B0), b1(B1), b2(B2), r(result);\n  for (size_t i = 0; i < N; i++)\n  {\n    if (std::fabs(r[i] - (a0[i] * b0[i] + a1[i] * b1[i] + a2[i] * b2[i])) > 1e-4f) return 1;\n  }\n  std::cout << std::fixed;")
patch_exact("${vendor_thrust_examples}.staging/histogram.cu"
  "    dense_histogram(input, histogram);"
  "    dense_histogram(input, histogram);\n    thrust::host_vector<int> dense(histogram);\n    int dense_total = 0;\n    for (size_t b = 0; b < dense.size(); b++)\n    {\n      int expected = 0;\n      for (int x : input) expected += x == (int)b;\n      if (dense[b] != expected) return 1;\n      dense_total += dense[b];\n    }\n    if (dense_total != N) return 1;")
patch_exact("${vendor_thrust_examples}.staging/histogram.cu"
  "    sparse_histogram(input, histogram_values, histogram_counts);"
  "    sparse_histogram(input, histogram_values, histogram_counts);\n    thrust::host_vector<int> values(histogram_values), counts(histogram_counts);\n    int sparse_total = 0;\n    for (size_t k = 0; k < values.size(); k++)\n    {\n      int expected = 0;\n      for (int x : input) expected += x == values[k];\n      if (counts[k] != expected) return 1;\n      sparse_total += counts[k];\n    }\n    if (sparse_total != N) return 1;")
if(vendor_cccl_name STREQUAL cccl_2_8)
  set(sort_greater "thrust::greater<int>()")
else()
  set(sort_greater "cuda::std::greater<int>()")
endif()
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "#include <iostream>"
  "#include <functional>\n#include <iostream>\n#include <thrust/host_vector.h>\n#include <utility>")
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "int main()"
  "template <typename T, typename Compare>\nbool verify_sorted(const thrust::device_vector<T>& before, const thrust::device_vector<T>& after, Compare comp)\n{\n  thrust::host_vector<T> expected(before), result(after);\n  bool sorted = thrust::is_sorted(result.begin(), result.end(), comp);\n  thrust::sort(expected.begin(), expected.end());\n  thrust::sort(result.begin(), result.end());\n  return sorted && expected == result;\n}\n\ntemplate <typename K, typename V, typename Compare>\nbool verify_sorted_by_key(const thrust::device_vector<K>& before_keys, const thrust::device_vector<V>& before_values,\n  const thrust::device_vector<K>& keys, const thrust::device_vector<V>& values, Compare comp)\n{\n  thrust::host_vector<K> bk(before_keys), k(keys);\n  thrust::host_vector<V> bv(before_values), v(values);\n  thrust::host_vector<std::pair<K, V>> expected(bk.size()), result(k.size());\n  for (size_t i = 0; i < k.size(); i++)\n  {\n    expected[i] = {bk[i], bv[i]};\n    result[i]   = {k[i], v[i]};\n  }\n  bool sorted = thrust::is_sorted(k.begin(), k.end(), comp);\n  thrust::sort(expected.begin(), expected.end());\n  thrust::sort(result.begin(), result.end());\n  return sorted && expected == result;\n}\n\nint main()")
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "    thrust::sort(keys.begin(), keys.end());"
  "    auto expected = keys;\n    thrust::sort(keys.begin(), keys.end());\n    if (!verify_sorted(expected, keys, std::less<>())) return 1;")
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "    thrust::sort(keys.begin(), keys.end(), evens_before_odds());"
  "    auto expected = keys;\n    thrust::sort(keys.begin(), keys.end(), evens_before_odds());\n    if (!verify_sorted(expected, keys, evens_before_odds())) return 1;")
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "    thrust::sort(keys.begin(), keys.end(), ${sort_greater});"
  "    auto expected = keys;\n    thrust::sort(keys.begin(), keys.end(), ${sort_greater});\n    if (!verify_sorted(expected, keys, ${sort_greater})) return 1;")
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "    thrust::sort_by_key(keys.begin(), keys.end(), values.begin());"
  "    auto before_keys = keys;\n    auto before_values = values;\n    thrust::sort_by_key(keys.begin(), keys.end(), values.begin());\n    if (!verify_sorted_by_key(before_keys, before_values, keys, values, std::less<>())) return 1;")
patch_exact("${vendor_thrust_examples}.staging/sort.cu"
  "    thrust::sort_by_key(keys.begin(), keys.end(), values.begin(), ${sort_greater});"
  "    auto before_keys = keys;\n    auto before_values = values;\n    thrust::sort_by_key(keys.begin(), keys.end(), values.begin(), ${sort_greater});\n    if (!verify_sorted_by_key(before_keys, before_values, keys, values, ${sort_greater})) return 1;")
commit_staged_source("${vendor_thrust_examples}.staging" "${vendor_thrust_examples}")

foreach(example IN ITEMS sum norm dot_products_with_zip saxpy sort histogram)
  set(vendor_release_tag)
  if(example MATCHES "^(saxpy|sort|histogram)$")
    set(vendor_release_tag "[release]")
  endif()
  conformance_vendor_cuda_cccl_case(thrust ${example} ${example}.cu
    ROOT "${vendor_thrust_examples}"
    INCLUDES "${vendor_thrust_examples}")
endforeach()
set(vendor_release_tag)
set(vendor_libcudacxx_tests)
if(vendor_architecture GREATER_EQUAL 60)
  list(APPEND vendor_libcudacxx_tests cuda/atomics/atomic.local cuda/atomics/atomic_ref_small
    std/atomics/atomics.types.generic/bool std/atomics/atomics.types.generic/floating_point)
endif()
if(vendor_architecture GREATER_EQUAL 70)
  list(APPEND vendor_libcudacxx_tests cuda/annotated_ptr/annotated_ptr cuda/annotated_ptr/access_property cuda/memcpy_async/group_memcpy_async_16b cuda/barrier/init cuda/barrier/native_handle
    cuda/pipeline_arrive_on std/thread/thread.barrier/arrive std/thread/thread.barrier/completion)
endif()
set(vendor_pipeline_arrive_on "${vendor_programs_directory}/cccl_pipeline_arrive_on")
foreach(test IN LISTS vendor_libcudacxx_tests)
  string(MAKE_C_IDENTIFIER "${test}" id)
  set(test_path "${test}")
  set(test_root "${vendor_cccl_root}")
  if(test STREQUAL "cuda/pipeline_arrive_on")
    if(EXISTS "${vendor_cccl_root}/libcudacxx/test/libcudacxx/cuda/pipeline/pipeline_arrive_on.pass.cpp")
      set(test_path cuda/pipeline/pipeline_arrive_on)
    endif()
    get_filename_component(test_file "${test_path}" NAME)
    file(REMOVE_RECURSE "${vendor_pipeline_arrive_on}.staging")
    file(COPY "${vendor_cccl_root}/libcudacxx/test/libcudacxx/${test_path}.pass.cpp" DESTINATION "${vendor_pipeline_arrive_on}.staging")
    patch_exact("${vendor_pipeline_arrive_on}.staging/${test_file}.pass.cpp"
      "using nvcuda::experimental::pipeline;"
      "using nvcuda::experimental::pipeline;\n\n__device__ unsigned pipeline_arrive_on_mismatches;")
    patch_exact("${vendor_pipeline_arrive_on}.staging/${test_file}.pass.cpp"
      "    assert(global_array[idx] == shared_array[idx]);"
      "    if (!(global_array[idx] == shared_array[idx])) atomicAdd(&pipeline_arrive_on_mismatches, 1u);")
    patch_exact("${vendor_pipeline_arrive_on}.staging/${test_file}.pass.cpp"
      "  return true;\n}"
      "  __syncthreads();\n  return pipeline_arrive_on_mismatches == 0;\n}")
    commit_staged_source("${vendor_pipeline_arrive_on}.staging" "${vendor_pipeline_arrive_on}")
    set(test_root "${vendor_pipeline_arrive_on}")
    set(test_path "${test_file}")
    set(test_relative "${test_file}.pass.cpp")
  else()
    set(test_relative "libcudacxx/test/libcudacxx/${test_path}.pass.cpp")
  endif()
  set(test_entry_includes "${vendor_cccl_root}/libcudacxx/test/libcudacxx/force_include.h")
  if(test STREQUAL "cuda/atomics/atomic.local" AND vendor_cccl_name STREQUAL cccl_2_8)
    set(local_atomics_header "${vendor_programs_directory}/cccl_local_atomics.h")
    file(WRITE "${local_atomics_header}.tmp" "#include <cuda/std/detail/__config>\n#undef _LIBCUDACXX_ATOMIC_UNSAFE_AUTOMATIC_STORAGE\n")
    file(COPY_FILE "${local_atomics_header}.tmp" "${local_atomics_header}" ONLY_IF_DIFFERENT)
    list(PREPEND test_entry_includes "${local_atomics_header}")
  endif()
  conformance_vendor_cuda_cccl_case(libcudacxx ${id} ${test_relative}
    ROOT "${test_root}"
    INCLUDES "${vendor_cccl_root}/libcudacxx/test/support"
    ENTRY_INCLUDES ${test_entry_includes})
endforeach()
